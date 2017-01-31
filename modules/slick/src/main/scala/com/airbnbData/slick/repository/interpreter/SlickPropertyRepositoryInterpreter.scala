package com.airbnbData.slick.repository.interpreter

import scalaz.Kleisli
import com.airbnbData.model.command.{AirbnbUserCommand, PropertyAndAirbnbUserCommand, PropertyDetailCommand}
import com.airbnbData.model.query.{AirbnbUser, Property, PropertyDetail}
import com.airbnbData.repository.PropertyRepository
import com.airbnbData.slick.dao.helper.{MyPostgresDriver, Profile}
import com.airbnbData.slick.dao.{AirbnbUsersDAO, PropertiesDAO}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import slick.basic.DatabasePublisher
import slick.jdbc.JdbcBackend.DatabaseDef
import com.airbnbData.slick.dao.helper.DBExecutor.implicits._


/**
  * Created by Lance on 2016-11-18.
  */

class SlickPropertyRepositoryInterpreter extends PropertyRepository with Profile with PropertiesDAO {

  val profile: MyPostgresDriver = MyPostgresDriver

  import profile.api._

  import com.airbnbData.model.command.Command
  import com.airbnbData.model.query.Query
  trait CommandMapper[C <: Command, DTOType <: com.airbnbData.slick.dao.helper.DTO[_]] {
    def convert(command: C): DTOType
  }

  trait QueryMapper[DTOType <: com.airbnbData.slick.dao.helper.DTO[_], Q <: Query] {
    def convert(dto: DTOType): Q
  }

  object Test extends CommandMapper[PropertyAndAirbnbUserCommand, PropertiesDAO.UserAndProperty] {

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

    def convert(property: PropertyAndAirbnbUserCommand): PropertiesDAO.UserAndProperty = {
      val airbnbUsersRow = LabelledGeneric[AirbnbUsersRow].from(
        LabelledGeneric[AirbnbUserCommand].to(property.belongsTo) ++
          Timestamp.gen.to(Timestamp())
      )

      val propertiesRow = LabelledGeneric[PropertiesRow].from(
        LabelledGeneric[PropertyDetailCommand].to(property.property) ++
          Timestamp.gen.to(Timestamp()) +
          ('airbnbUserId ->> property.belongsTo.id)
      )

      PropertiesDAO.UserAndProperty(airbnbUsersRow, propertiesRow)
    }
  }

  object Y extends QueryMapper[PropertiesDAO.UserAndProperty, ]

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

    def convert(property: PropertyAndAirbnbUserCommand): PropertiesDAO.UserAndProperty = {
      val airbnbUsersRow = LabelledGeneric[AirbnbUsersRow].from(
        LabelledGeneric[AirbnbUserCommand].to(property.belongsTo) ++
          Timestamp.gen.to(Timestamp())
      )

      val propertiesRow = LabelledGeneric[PropertiesRow].from(
        LabelledGeneric[PropertyDetailCommand].to(property.property) ++
          Timestamp.gen.to(Timestamp()) +
          ('airbnbUserId ->> property.belongsTo.id)
      )

      PropertiesDAO.UserAndProperty(airbnbUsersRow, propertiesRow)
    }

    def convert(userAndProperty: PropertiesDAO.UserAndProperty): Property = {
      val property = userAndProperty.property
      val user = userAndProperty.user

      val propertyDetail = PropertyDetail(
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
        property.createdAt,
        property.updatedAt
      )
      val userDetail = AirbnbUser(
        user.id,
        user.firstName,
        user.about,
        user.document,
        user.createdAt,
        user.updatedAt
      )

      Property(propertyDetail, userDetail)
    }
  }

  override def all(): ObservableOp[Property] = {
    Kleisli { db =>
      Observable.fromReactivePublisher(db.stream(PropertiesDAO.streamingGetAll))
        .map { case (prop, user) => Mapper.convert(PropertiesDAO.UserAndProperty(user, prop)) }
    }
  }

  override def create(property: PropertyAndAirbnbUserCommand): TaskOp[Int] =
    Kleisli { db =>
      Task.defer { Task.fromFuture(
        db.run(
          PropertiesDAO.insertOrUpdate(Mapper.convert(property))
        )
      )}
    }

  override def bulkCreate(list: Seq[PropertyAndAirbnbUserCommand]): TaskOp[Int] = {
    Kleisli { db =>
      Task.defer { Task.fromFuture(
        db.run(
          PropertiesDAO.bulkInsertOrUpdate(list.map(Mapper.convert))
        )
      )}
    }
  }

  override def deleteAll(): TaskOp[Int] = {
    Kleisli { db =>
      val deletion = PropertiesDAO.deleteAll

      Task.defer { Task.fromFuture(db.run(deletion)) }
    }
  }
}

import scala.concurrent.Future
import scala.language.implicitConversions

object FutureOp {
  implicit def futureToMonixTask[T](future: Future[T]): Task[T] =
    Task.defer { Task.fromFuture(future) }
}