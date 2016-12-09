package com.airbnbData.slick.repository.interpreter

import scala.language.implicitConversions
import org.joda.time.DateTime

import scalaz.Kleisli
import com.airbnbData.model._
import com.airbnbData.repository.PropertyRepository
import com.airbnbData.slick.dao.helper.{MyPostgresDriver, Profile}
import com.airbnbData.slick.dao.{AirbnbUserPropertiesDAO, AirbnbUsersDAO, PropertiesDAO}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import slick.jdbc.JdbcBackend.DatabaseDef
import scala.util.{Failure, Success}


/**
  * Created by Lance on 2016-11-18.
  */
class SlickPropertyRepositoryInterpreter
  extends PropertyRepository with Profile with AirbnbUsersDAO with PropertiesDAO with AirbnbUserPropertiesDAO {

  // Use the custom postgresql driver.
  override val profile: MyPostgresDriver = MyPostgresDriver

  import profile.api._

  private def insert(property: PropertyAndAirbnbUserCreation): DBIO[Int] = {

    // TODO: Could refactor the id extraction part into something generic
    for {
      _ <- (Properties returning Properties.map(_.id) insertOrUpdate property.property).asTry
      // Check if user is already present in database
      _ <- (AirbnbUsers returning AirbnbUsers.map(_.id) insertOrUpdate property.belongsTo).asTry
      // FIXME: Cannot use `upsert` due to a bug here: https://github.com/slick/slick/issues/966
      count <- (AirbnbUserProperties += AirbnbUserPropertiesRow(
        property.belongsTo.id,
        property.property.id
      )).asTry
    } yield count match {
      case Success(v) => v
      case Failure(e) =>
        println(e)
        0
    }
  }

  override def create(property: PropertyAndAirbnbUserCreation): TaskOp[Int] =
    Kleisli { db =>
      Task.defer {
        Task.fromFuture(
          db.run(
            insert(property).transactionally
          )
        )
      }
    }

  override def obv_create: (PropertyAndAirbnbUserCreation) => ObservableOp[Int] =
    create(_).mapT[Observable, Int](Observable.fromTask)

  override def bulkCreate(list: Seq[PropertyAndAirbnbUserCreation]): TaskOp[Int] = {
    Kleisli { db =>
      Task.defer {
        Task
          .fromFuture(
            db.run(
              DBIO.sequence(
                list.map(insert(_).transactionally)
              )
            )
          )
      }
        .map(_.sum)
    }
  }

  override def obv_bulkCreate: (Seq[PropertyAndAirbnbUserCreation]) => ObservableOp[Int] =
    bulkCreate(_).mapT[Observable, Int](Observable.fromTask)

  override def deleteAll(): TaskOp[Int] = {
    Kleisli { db =>

      val deletion = AirbnbUserProperties.delete andThen Properties.delete andThen AirbnbUsers.delete

      Task.defer { Task.fromFuture(db.run(deletion)) }
    }
  }

  override def close(): TaskOp[Unit] =
    Kleisli { db =>
      Task { db.close() }
    }
}
