package com.airbnbData.slick.repository.interpreter

import scala.language.implicitConversions
import org.joda.time.DateTime
import scalaz.Kleisli
import com.airbnbData.model._
import com.airbnbData.repository.{PropertyRepository, PropertyRepositoryExecutionContext}
import com.airbnbData.slick.dao.helper.{MyPostgresDriver, Profile}
import com.airbnbData.slick.dao.{AirbnbUserPropertiesDAO, AirbnbUsersDAO, PropertiesDAO}
import monix.eval.Task


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

      Task.fromFuture(
        db.run(
          createRelation.transactionally
        )
      )
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

      Task.fromFuture(
        db.run(
          createRelations
        )
      )
    }
  }

  override def deleteAll() = {
    Kleisli { case (db, ec) =>
      implicit val context = ec

      val deletion = AirbnbUserProperties.delete andThen Properties.delete andThen AirbnbUsers.delete

      Task.fromFuture(db.run(deletion))
    }
  }

  // TODO: Refactor close function
  override def close(): Kleisli[Task, slick.jdbc.JdbcBackend.Database, Unit] =
    Kleisli { db =>
      Task { db.close() }
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
