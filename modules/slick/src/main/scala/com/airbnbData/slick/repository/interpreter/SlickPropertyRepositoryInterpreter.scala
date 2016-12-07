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
      count <- (AirbnbUserProperties += AirbnbUserPropertyRow(
        property.belongsTo.id,
        property.property.id,
        DateTime.now,
        Some(DateTime.now)
      )).asTry
    } yield count match {
      case Success(v) => v
      case Failure(_) => 0
    }
  }

  override def create(property: PropertyAndAirbnbUserCreation): Operation[Int] =
    Kleisli { db =>
      Task.fromFuture(
        db.run(
          insert(property).transactionally
        )
      )
    }

  override def bulkCreate(list: Seq[PropertyAndAirbnbUserCreation]): Operation[Int] = {
    Kleisli { db =>
      Task
        .fromFuture(
          db.run(
            DBIO.sequence(
              list.map(insert(_).transactionally)
            )
          )
        )
        .map(_.sum)
    }
  }

  override def deleteAll(): Operation[Int] = {
    Kleisli { db =>

      val deletion = AirbnbUserProperties.delete andThen Properties.delete andThen AirbnbUsers.delete

      Task.fromFuture(db.run(deletion))
    }
  }

  // TODO: Refactor close function
  override def close(): Operation[Unit] =
    Kleisli { db =>
      Task { db.close() }
    }

  private implicit def airbnbUserToAirbnbUsersRow(user: AirbnbUserCreation): AirbnbUserRow = {
    AirbnbUserRow(
      user.id,
      user.firstName,
      user.about,
      user.document,
      DateTime.now,
      Some(DateTime.now)
    )
  }

  private implicit def propertyCreationToPropertiesRow(property: PropertyDetailCreation): PropertyRow = {
    PropertyRow(
      property.id,
      property.bathrooms,
      property.bedrooms,
      property.beds,
      property.city,
      property.name,
      property.personCapacity,
      property.propertyType,
      property.publicAddress,
      property.roomType,
      property.document,
      property.summary,
      property.address,
      property.description,
      property.airbnbUrl,
      DateTime.now,
      Some(DateTime.now)
    )
  }
}
