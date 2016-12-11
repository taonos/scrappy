package com.airbnbData.slick.repository.interpreter

import scalaz.Kleisli
import com.airbnbData.model._
import com.airbnbData.repository.PropertyRepository
import com.airbnbData.slick.dao.helper.{DTO, MyPostgresDriver, Profile}
import com.airbnbData.slick.dao.{AirbnbUsersDAO, PropertiesDAO}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import slick.jdbc.JdbcBackend.DatabaseDef

import scala.util.{Failure, Success}


/**
  * Created by Lance on 2016-11-18.
  */

class SlickPropertyRepositoryInterpreter
  extends PropertyRepository with Profile with AirbnbUsersDAO with PropertiesDAO {

  // Use the custom postgresql driver.
  override val profile: MyPostgresDriver = MyPostgresDriver

  import profile.api._

  private object Mapper {
    import shapeless.LabelledGeneric
    import shapeless.record._
    import shapeless.ops.record._
    import shapeless.syntax.singleton._
    import org.joda.time.DateTime

    private case class Timestamp(createdAt: DateTime = DateTime.now(), updatedAt: DateTime = DateTime.now())

    def convert(property: PropertyAndAirbnbUserCreation) = {
      val airbnbUsersRow = LabelledGeneric[AirbnbUsersRow].from(
        LabelledGeneric[AirbnbUserCreation].to(property.belongsTo) ++
          LabelledGeneric[Timestamp].to(Timestamp())
      )

      val propertiesRow = LabelledGeneric[PropertiesRow].from(
        LabelledGeneric[PropertyDetailCreation].to(property.property) ++
          LabelledGeneric[Timestamp].to(Timestamp()) +
          ('airbnbUserId ->> property.belongsTo.id)
      )

      (propertiesRow, airbnbUsersRow)
    }
  }

  private def insert(property: PropertyAndAirbnbUserCreation): DBIO[Int] = {
    val (prop, user) = Mapper.convert(property)
    for {
      _ <- AirbnbUsers insertOrUpdate user
      _ <- Properties insertOrUpdate prop
    } yield 1
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

  override def deleteAll(): TaskOp[Int] = {
    Kleisli { db =>

      val deletion = Properties.delete andThen AirbnbUsers.delete

      Task.defer { Task.fromFuture(db.run(deletion)) }
    }
  }

  override def close(): TaskOp[Unit] =
    Kleisli { db =>
      Task { db.close() }
    }
}
