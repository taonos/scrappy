package com.airbnbData.slick.repository.interpreter

import scala.concurrent.Future
import scala.language.implicitConversions
import org.joda.time.DateTime
import slick.jdbc.JdbcBackend.Database
import scalaz.concurrent.Task
import scalaz.Kleisli
import com.airbnbData.util.FutureOps.Implicits._
import com.airbnbData.model._
import com.airbnbData.repository.{PropertyRepository, PropertyRepositoryExecutionContext}
import com.airbnbData.slick.dao.helper.{MyPostgresDriver, Profile}
import com.airbnbData.slick.dao.{AirbnbUserPropertiesDAO, AirbnbUsersDAO, PropertiesDAO}


/**
  * Created by Lance on 2016-11-18.
  */
class SlickPropertyRepositoryInterpreter
  extends PropertyRepository with Profile with AirbnbUsersDAO with PropertiesDAO with AirbnbUserPropertiesDAO {

  // Use the custom postgresql driver.
  override val profile: MyPostgresDriver = MyPostgresDriver

  import profile.api._

  override def create(user: AirbnbUserCreation, property: PropertyCreation): Operation[Int] =
    Kleisli { case (db, ec) =>
      implicit val context = ec
      // TODO: Could refactor the id extraction part into something generic
      val createProperty = Properties returning Properties.map(_.id) += property
      val createAirbnbUser = AirbnbUsers returning AirbnbUsers.map(_.id) += user

      val createRelation = (createProperty zip createAirbnbUser)
        .flatMap { case (pid, uid) =>
          AirbnbUserProperties += AirbnbUserPropertyRow(uid, pid, DateTime.now)
        }

      db
        .run(
          createRelation.transactionally
        )
        .asTask
    }

  override def bulkCreate(list: Seq[(AirbnbUserCreation, PropertyCreation)]): Operation[Option[Int]] = {
    Kleisli { case (db, ec) =>
      implicit val context = ec
      // TODO: Could refactor the id extraction part into something generic
      val users = list
        .map { case (a, _) => airbnbUserToAirbnbUsersRow(a) }
      val props = list
        .map { case (_, p) => propertyCreationToPropertiesRow(p) }

      val createProperty = Properties returning Properties.map(_.id) ++= props
      val createAirbnbUser = AirbnbUsers returning AirbnbUsers.map(_.id) ++= users

      val createRelations = (createProperty zip createAirbnbUser)
        .flatMap { case (pid, uid) =>
          val usersAndProperties = pid zip uid map { case (p, u) =>
            AirbnbUserPropertyRow(u, p, DateTime.now)
          }
          AirbnbUserProperties ++= usersAndProperties
        }

      db
        .run(
          createRelations
        )
        .asTask
    }
  }

  // TODO: Refactor close function
  override def close(): Kleisli[Task, slick.jdbc.JdbcBackend.Database, Unit] =
    Kleisli { db =>
      Task.now(db.close())
    }

  private implicit def airbnbUserToAirbnbUsersRow(user: AirbnbUserCreation): AirbnbUserRow = {
    AirbnbUserRow(
      user.id,
      user.firstName,
      user.about,
      user.document,
      DateTime.now
    )
  }

  private implicit def propertyCreationToPropertiesRow(property: PropertyCreation): PropertyRow = {
    PropertyRow(
      property.id,
      property.bathrooms,
      property.bedrooms,
      property.beds,
      property.city,
      property.instantBookable,
      property.isBusinessTravelReady,
      property.isNewListing,
      property.geopoint,
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
      DateTime.now
    )
  }
}
