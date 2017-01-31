package com.airbnbData.slick.dao

import java.net.URL

import com.airbnbData.slick.dao.helper._
import io.circe.Json
import org.joda.time.DateTime
import slick.ast.BaseTypedType
import slick.lifted.ForeignKeyQuery
import slick.sql.SqlProfile.ColumnOption.SqlType

/**
  * Created by Lance on 2016-10-12.
  */

trait PropertiesDAO extends AirbnbUsersDAO { self: Profile =>
  import java.net.URL
  import com.vividsolutions.jts.geom.Point
  import slick.model.ForeignKeyAction
  import slick.model
  import org.joda.time.DateTime
  import io.circe.Json
  import profile.api._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table Properties
    *  @param id Database column id SqlType(int8), PrimaryKey
    *  @param bathrooms Database column bathrooms SqlType(int4), Default(0)
    *  @param bedrooms Database column bedrooms SqlType(int4), Default(0)
    *  @param beds Database column beds SqlType(int4), Default(0)
    *  @param city Database column city SqlType(varchar), Length(50,true)
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
    *  @param updatedAt Database column updated_at SqlType(timestamptz)
    *  @param airbnbUserId Database column airbnb_user_id SqlType(int8) */
  case class PropertiesRow(override val id: Long, bathrooms: Int = 0, bedrooms: Int = 0, beds: Int = 0, city: String, name: String, personCapacity: Int = 0, propertyType: String, publicAddress: String, roomType: String, document: Json, summary: String, address: String, description: String, airbnbUrl: URL, createdAt: DateTime = DateTime.now(), updatedAt: DateTime = DateTime.now(), airbnbUserId: Long)
    extends DTO[Long]

  /** GetResult implicit for fetching PropertyRow objects using plain SQL queries */
  implicit def getResultPropertiesRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[String], e3: GR[Json], e4: GR[URL], e5: GR[DateTime]): GR[PropertiesRow] = GR{
    prs => import prs._
      PropertiesRow.tupled((<<[Long], <<[Int], <<[Int], <<[Int], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[String], <<[Json], <<[String], <<[String], <<[String], <<[URL], <<[DateTime], <<[DateTime], <<[Long]))
  }
  /** Table description of table properties. Objects of this class serve as prototypes for rows in queries. */
  protected class PropertiesTable(_tableTag: Tag) extends Table[PropertiesRow](_tableTag, "properties") with Keyed[Long] {
    def * = (id, bathrooms, bedrooms, beds, city, name, personCapacity, propertyType, publicAddress, roomType, document, summary, address, description, airbnbUrl, createdAt, updatedAt, airbnbUserId) <> (PropertiesRow.tupled, PropertiesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(bathrooms), Rep.Some(bedrooms), Rep.Some(beds), Rep.Some(city), Rep.Some(name), Rep.Some(personCapacity), Rep.Some(propertyType), Rep.Some(publicAddress), Rep.Some(roomType), Rep.Some(document), Rep.Some(summary), Rep.Some(address), Rep.Some(description), Rep.Some(airbnbUrl), Rep.Some(createdAt), Rep.Some(updatedAt), Rep.Some(airbnbUserId)).shaped.<>({r=>import r._; _1.map(_=> PropertiesRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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
    val document: Rep[Json] = column[Json]("document")
    /** Database column summary SqlType(text) */
    val summary: Rep[String] = column[String]("summary")
    /** Database column address SqlType(varchar), Length(200,true) */
    val address: Rep[String] = column[String]("address", O.Length(200,varying=true))
    /** Database column description SqlType(text) */
    val description: Rep[String] = column[String]("description")
    /** Database column airbnb_url SqlType(varchar), Length(2038,true) */
    val airbnbUrl: Rep[URL] = column[URL]("airbnb_url", O.Length(2038,varying=true))
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at", SqlType("timestamp with time zone default CURRENT_TIMESTAMP"))
    /** Database column updated_at SqlType(timestamptz) */
    val updatedAt: Rep[DateTime] = column[DateTime]("updated_at", SqlType("timestamp with time zone default CURRENT_TIMESTAMP"))
    /** Database column airbnb_user_id SqlType(int8) */
    val airbnbUserId: Rep[Long] = column[Long]("airbnb_user_id")

    /** Foreign key referencing AirbnbUser (database name user_id_FK) */
    lazy val airbnbUserFk = foreignKey("airbnb_user_id_fk", airbnbUserId, AirbnbUsers)(r => r.id, onUpdate=model.ForeignKeyAction.NoAction, onDelete=model.ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table PropertyTable */
  lazy val Properties = new TableQuery(tag => new PropertiesTable(tag))

  object PropertiesDAO extends DAO[PropertiesTable, PropertiesRow, Long](profile) {

    val tableQuery = Properties

    case class UserAndProperty(user: AirbnbUsersRow, property: PropertiesRow)

    private lazy val joinedQuery = Properties join AirbnbUsers on { case (p, u) => p.airbnbUserId === u.id }

    override def streamingGetAll: StreamingDBIO[Seq[(PropertiesRow, AirbnbUsersRow)], (PropertiesRow, AirbnbUsersRow)] = {
      joinedQuery.result
    }

    def insertOrUpdate(row: UserAndProperty): DBIO[Int] = {
      (for {
        _ <- AirbnbUsers insertOrUpdate row.user
        _ <- Properties insertOrUpdate row.property
      } yield 1).transactionally
    }

    def bulkInsertOrUpdate(rows: Seq[UserAndProperty]): DBIO[Int] =
      DBIO
        .sequence(rows.map(insertOrUpdate))
        .map(_.sum)

    def insert(row: UserAndProperty): DBIO[Int] =
      (for {
        _ <- AirbnbUsers += row.user
        _ <- Properties += row.property
      } yield 1).transactionally

    override def deleteAll: DBIO[Int] =
      AirbnbUsersDAO.deleteAll andThen super.deleteAll
  }
}