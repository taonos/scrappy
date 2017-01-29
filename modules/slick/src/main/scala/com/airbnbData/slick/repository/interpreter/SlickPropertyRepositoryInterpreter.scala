package com.airbnbData.slick.repository.interpreter

import scalaz.Kleisli
import com.airbnbData.model._
import com.airbnbData.model.command.{AirbnbUserCommand, PropertyAndAirbnbUserCommand, PropertyDetailCommand}
import com.airbnbData.model.query.{AirbnbUser, Property, PropertyDetail}
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
    import shapeless._
    import shapeless.record._
    import shapeless.ops.record._
    import shapeless.ops.hlist
    import shapeless.syntax.singleton._
    import org.joda.time.DateTime

    private case class Timestamp(createdAt: DateTime = DateTime.now(), updatedAt: DateTime = DateTime.now())

    private object Timestamp {

      implicit val gen = LabelledGeneric[Timestamp]
    }

    def convert(property: PropertyAndAirbnbUserCommand): (PropertiesRow, AirbnbUsersRow) = {
      val airbnbUsersRow = LabelledGeneric[AirbnbUsersRow].from(
        LabelledGeneric[AirbnbUserCommand].to(property.belongsTo) ++
          Timestamp.gen.to(Timestamp())
      )

      val propertiesRow = LabelledGeneric[PropertiesRow].from(
        LabelledGeneric[PropertyDetailCommand].to(property.property) ++
          Timestamp.gen.to(Timestamp()) +
          ('airbnbUserId ->> property.belongsTo.id)
      )

      (propertiesRow, airbnbUsersRow)
    }

    def convert(propertiesRow: PropertiesRow, airbnbUsersRow: AirbnbUsersRow): Property = {
      val detail = PropertyDetail(
        propertiesRow.id,
        propertiesRow.bathrooms,
        propertiesRow.bedrooms,
        propertiesRow.beds,
        propertiesRow.city,
        propertiesRow.name,
        propertiesRow.personCapacity,
        propertiesRow.propertyType,
        propertiesRow.publicAddress,
        propertiesRow.roomType,
        propertiesRow.document,
        propertiesRow.summary,
        propertiesRow.address,
        propertiesRow.description,
        propertiesRow.airbnbUrl,
        propertiesRow.createdAt,
        propertiesRow.updatedAt
      )
      val user = AirbnbUser(
        airbnbUsersRow.id,
        airbnbUsersRow.firstName,
        airbnbUsersRow.about,
        airbnbUsersRow.document,
        airbnbUsersRow.createdAt,
        airbnbUsersRow.updatedAt
      )
      Property(detail, user)
    }
  }

  private def insert(property: PropertyAndAirbnbUserCommand): DBIO[Int] = {
    val (prop, user) = Mapper.convert(property)
    for {
      _ <- AirbnbUsers insertOrUpdate user
      _ <- Properties insertOrUpdate prop
    } yield 1
  }

  override def all(): ObservableOp[Property] = {
    Kleisli { db =>

      val query = for {
        props <- Properties
        users <- props.airbnbUserFk
      } yield (props, users)

      val result = db.stream(query.result)

      Observable.fromReactivePublisher(result)
        .map { case (prop, user) => Mapper.convert(prop, user) }
    }
  }

  override def create(property: PropertyAndAirbnbUserCommand): TaskOp[Int] =
    Kleisli { db =>
      Task.defer {
        Task.fromFuture(
          db.run(
            insert(property).transactionally
          )
        )
      }
    }

  override def bulkCreate(list: Seq[PropertyAndAirbnbUserCommand]): TaskOp[Int] = {
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
