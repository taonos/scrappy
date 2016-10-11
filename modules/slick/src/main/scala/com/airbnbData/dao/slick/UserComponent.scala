package com.airbnbData.dao.slick

import slick.lifted.MappedTo

/**
  * Created by Lance on 2016-10-11.
  */

trait Profile {
  val profile: MyPostgresDriver
}

trait UsersComponent { self: Profile =>
  import profile.api._
  import org.joda.time.DateTime
  import play.api.libs.json.{JsValue, Json}
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table Users
    *  @param id Database column id SqlType(uuid), PrimaryKey
    *  @param email Database column email SqlType(varchar), Length(1024,true)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class UsersRow(id: java.util.UUID, email: String, createdAt: DateTime, updatedAt: Option[DateTime] = None)
//  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
//  implicit def GetResultUsersRow(implicit e0: GR[java.util.UUID], e1: GR[String], e2: GR[DateTime], e3: GR[Option[DateTime]]): GR[UsersRow] = GR{
//    prs => import prs._
//      UsersRow.tupled((<<[java.util.UUID], <<[String], <<[DateTime], <<?[DateTime], <<[JsValue]))
//  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends Table[UsersRow](_tableTag, "users") {
    def * = (id, email, createdAt, updatedAt) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(email), Rep.Some(createdAt), updatedAt).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(uuid), PrimaryKey */
    val id: Rep[java.util.UUID] = column[java.util.UUID]("id", O.PrimaryKey)
    /** Database column email SqlType(varchar), Length(1024,true) */
    val email: Rep[String] = column[String]("email", O.Length(1024,varying=true))
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("updated_at", O.Default(None))
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}

trait AirbnbUserComponent { self: Profile =>
  import profile.api._
  import org.joda.time.DateTime
  import play.api.libs.json.{JsValue, Json}
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table AirbnbUser
    *  @param id Database column id SqlType(int8), PrimaryKey
    *  @param firstName Database column first_name SqlType(varchar), Length(50,true)
    *  @param about Database column about SqlType(text)
    *  @param document Database column document SqlType(jsonb), Length(2147483647,false)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class AirbnbUserRow(id: PK[AirbnbUser], firstName: String, about: String, document: JsValue, createdAt: DateTime, updatedAt: Option[DateTime] = None)
//  /** GetResult implicit for fetching AirbnbUserRow objects using plain SQL queries */
//  implicit def GetResultAirbnbUserRow(implicit e0: GR[Long], e1: GR[String], e2: GR[DateTime], e3: GR[Option[DateTime]]): GR[AirbnbUserRow] = GR{
//    prs => import prs._
//      AirbnbUserRow.tupled((<<[Long], <<[String], <<[String], <<[String], <<[DateTime], <<?[DateTime]))
//  }
  /** Table description of table airbnb_user. Objects of this class serve as prototypes for rows in queries. */
  class AirbnbUser(_tableTag: Tag) extends Table[AirbnbUserRow](_tableTag, "airbnb_user") {
    def * = (id, firstName, about, document, createdAt, updatedAt) <> (AirbnbUserRow.tupled, AirbnbUserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(firstName), Rep.Some(about), Rep.Some(document), Rep.Some(createdAt), updatedAt).shaped.<>({r=>import r._; _1.map(_=> AirbnbUserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int8), PrimaryKey */
    val id: Rep[PK[AirbnbUser]] = column[PK[AirbnbUser]]("id", O.PrimaryKey)
    /** Database column first_name SqlType(varchar), Length(50,true) */
    val firstName: Rep[String] = column[String]("first_name", O.Length(50,varying=true))
    /** Database column about SqlType(text) */
    val about: Rep[String] = column[String]("about")
    /** Database column document SqlType(jsonb), Length(2147483647,false) */
    val document: Rep[JsValue] = column[JsValue]("document")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("updated_at", O.Default(None))
  }
  /** Collection-like TableQuery object for table AirbnbUser */
  lazy val AirbnbUser = new TableQuery(tag => new AirbnbUser(tag))
}

object AirbnbUserComponent extends Profile with AirbnbUserComponent {
  override val profile: MyPostgresDriver = MyPostgresDriver
}

trait PropertyComponent { self: Profile =>
  import profile.api._
  import org.joda.time.DateTime
  import play.api.libs.json.{JsValue, Json}
  import com.vividsolutions.jts.geom.{Geometry, Point}
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table Property
    *  @param id Database column id SqlType(int8), PrimaryKey
    *  @param bathrooms Database column bathrooms SqlType(int4), Default(0)
    *  @param bedrooms Database column bedrooms SqlType(int4), Default(0)
    *  @param beds Database column beds SqlType(int4), Default(0)
    *  @param city Database column city SqlType(varchar), Length(50,true)
    *  @param instantBookable Database column instant_bookable SqlType(bool), Default(false)
    *  @param isBusinessTravelReady Database column is_business_travel_ready SqlType(bool), Default(false)
    *  @param isNewListing Database column is_new_listing SqlType(bool), Default(false)
    *  @param geopoint Database column geopoint SqlType(point), Length(2147483647,false)
    *  @param name Database column name SqlType(varchar), Length(200,true)
    *  @param personCapacity Database column person_capacity SqlType(int4), Default(0)
    *  @param propertyType Database column property_type SqlType(varchar)
    *  @param publicAddress Database column public_address SqlType(varchar), Length(200,true)
    *  @param roomType Database column room_type SqlType(varchar), Length(70,true)
    *  @param document Database column document SqlType(jsonb), Length(2147483647,false)
    *  @param summary Database column summary SqlType(text)
    *  @param address Database column address SqlType(varchar), Length(200,true)
    *  @param description Database column description SqlType(text)
    *  @param airbnbUrl Database column airbnb_url SqlType(varchar), Length(2038,true)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class PropertyRow(id: Long, bathrooms: Int = 0, bedrooms: Int = 0, beds: Int = 0, city: String, instantBookable: Boolean = false, isBusinessTravelReady: Boolean = false, isNewListing: Boolean = false, geopoint: Point, name: String, personCapacity: Int = 0, propertyType: String, publicAddress: String, roomType: String, document: JsValue, summary: String, address: String, description: String, airbnbUrl: String, createdAt: DateTime, updatedAt: Option[DateTime] = None)
//  /** GetResult implicit for fetching PropertyRow objects using plain SQL queries */
//  implicit def GetResultPropertyRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[String], e3: GR[Boolean], e4: GR[DateTime], e5: GR[Option[DateTime]]): GR[PropertyRow] = GR{
//    prs => import prs._
//      PropertyRow.tupled((<<[Long], <<[Int], <<[Int], <<[Int], <<[String], <<[Boolean], <<[Boolean], <<[Boolean], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[DateTime], <<?[DateTime]))
//  }
  /** Table description of table property. Objects of this class serve as prototypes for rows in queries. */
  class Property(_tableTag: Tag) extends Table[PropertyRow](_tableTag, "property") {
    def * = (id, bathrooms, bedrooms, beds, city, instantBookable, isBusinessTravelReady, isNewListing, geopoint, name, personCapacity, propertyType, publicAddress, roomType, document, summary, address, description, airbnbUrl, createdAt, updatedAt) <> (PropertyRow.tupled, PropertyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(bathrooms), Rep.Some(bedrooms), Rep.Some(beds), Rep.Some(city), Rep.Some(instantBookable), Rep.Some(isBusinessTravelReady), Rep.Some(isNewListing), Rep.Some(geopoint), Rep.Some(name), Rep.Some(personCapacity), Rep.Some(propertyType), Rep.Some(publicAddress), Rep.Some(roomType), Rep.Some(document), Rep.Some(summary), Rep.Some(address), Rep.Some(description), Rep.Some(airbnbUrl), Rep.Some(createdAt), updatedAt).shaped.<>({r=>import r._; _1.map(_=> PropertyRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get, _21)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int8), PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.PrimaryKey)
    /** Database column bathrooms SqlType(int4), Default(0) */
    val bathrooms: Rep[Int] = column[Int]("bathrooms", O.Default(0))
    /** Database column bedrooms SqlType(int4), Default(0) */
    val bedrooms: Rep[Int] = column[Int]("bedrooms", O.Default(0))
    /** Database column beds SqlType(int4), Default(0) */
    val beds: Rep[Int] = column[Int]("beds", O.Default(0))
    /** Database column city SqlType(varchar), Length(50,true) */
    val city: Rep[String] = column[String]("city", O.Length(50,varying=true))
    /** Database column instant_bookable SqlType(bool), Default(false) */
    val instantBookable: Rep[Boolean] = column[Boolean]("instant_bookable", O.Default(false))
    /** Database column is_business_travel_ready SqlType(bool), Default(false) */
    val isBusinessTravelReady: Rep[Boolean] = column[Boolean]("is_business_travel_ready", O.Default(false))
    /** Database column is_new_listing SqlType(bool), Default(false) */
    val isNewListing: Rep[Boolean] = column[Boolean]("is_new_listing", O.Default(false))
    /** Database column geopoint SqlType(point), Length(2147483647,false) */
    val geopoint: Rep[Point] = column[Point]("geopoint")
    /** Database column name SqlType(varchar), Length(200,true) */
    val name: Rep[String] = column[String]("name", O.Length(200,varying=true))
    /** Database column person_capacity SqlType(int4), Default(0) */
    val personCapacity: Rep[Int] = column[Int]("person_capacity", O.Default(0))
    /** Database column property_type SqlType(varchar) */
    val propertyType: Rep[String] = column[String]("property_type")
    /** Database column public_address SqlType(varchar), Length(200,true) */
    val publicAddress: Rep[String] = column[String]("public_address", O.Length(200,varying=true))
    /** Database column room_type SqlType(varchar), Length(70,true) */
    val roomType: Rep[String] = column[String]("room_type", O.Length(70,varying=true))
    /** Database column document SqlType(jsonb), Length(2147483647,false) */
    val document: Rep[JsValue] = column[JsValue]("document")
    /** Database column summary SqlType(text) */
    val summary: Rep[String] = column[String]("summary")
    /** Database column address SqlType(varchar), Length(200,true) */
    val address: Rep[String] = column[String]("address", O.Length(200,varying=true))
    /** Database column description SqlType(text) */
    val description: Rep[String] = column[String]("description")
    /** Database column airbnb_url SqlType(varchar), Length(2038,true) */
    val airbnbUrl: Rep[String] = column[String]("airbnb_url", O.Length(2038,varying=true))
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("updated_at", O.Default(None))
  }
  /** Collection-like TableQuery object for table Property */
  lazy val Property = new TableQuery(tag => new Property(tag))

}

object PropertyComponent extends Profile with PropertyComponent {
  override val profile: MyPostgresDriver = MyPostgresDriver
}

trait UserPropertyComponent { self: Profile =>
  import profile.api._
  import org.joda.time.DateTime
  import play.api.libs.json.{JsValue, Json}
  import com.vividsolutions.jts.geom.{Geometry, Point}
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}


  import AirbnbUserComponent.AirbnbUser
  import PropertyComponent.Property

  /** Entity class storing rows of table UserProperty
    *  @param userId Database column user_id SqlType(int8)
    *  @param propertyId Database column property_id SqlType(int8)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class UserPropertyRow(userId: PK[AirbnbUser], propertyId: Long, createdAt: DateTime, updatedAt: Option[DateTime] = None)
//  /** GetResult implicit for fetching UserPropertyRow objects using plain SQL queries */
//  implicit def GetResultUserPropertyRow(implicit e0: GR[Long], e1: GR[DateTime], e2: GR[Option[DateTime]]): GR[UserPropertyRow] = GR{
//    prs => import prs._
//      UserPropertyRow.tupled((<<[Long], <<[Long], <<[DateTime], <<?[DateTime]))
//  }
  /** Table description of table user_property. Objects of this class serve as prototypes for rows in queries. */
  class UserProperty(_tableTag: Tag) extends Table[UserPropertyRow](_tableTag, "user_property") {
    def * = (userId, propertyId, createdAt, updatedAt) <> (UserPropertyRow.tupled, UserPropertyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(propertyId), Rep.Some(createdAt), updatedAt).shaped.<>({r=>import r._; _1.map(_=> UserPropertyRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(int8) */
    val userId: Rep[PK[AirbnbUser]] = column[PK[AirbnbUser]]("user_id")
    /** Database column property_id SqlType(int8) */
    val propertyId: Rep[Long] = column[Long]("property_id")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("updated_at", O.Default(None))

    /** Primary key of UserProperty (database name user_property_pkey) */
    val pk = primaryKey("user_property_pkey", (propertyId, userId))

    /** Foreign key referencing AirbnbUser (database name user_id_FK) */
    lazy val airbnbUserFk = foreignKey("user_id_FK", userId, AirbnbUser)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Restrict)
    /** Foreign key referencing Property (database name property_id_FK) */
    lazy val propertyFk = foreignKey("property_id_FK", propertyId, Property)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table UserProperty */
  lazy val UserProperty = new TableQuery(tag => new UserProperty(tag))

}

final case class PK[A](value: Long) extends AnyVal with MappedTo[Long]
