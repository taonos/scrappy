package com.airbnbData.slick.dao

import com.airbnbData.slick.dao.helper._
import io.circe.Json
import org.joda.time.DateTime
import slick.ast.BaseTypedType
import slick.lifted.AppliedCompiledFunction
import slick.sql.SqlProfile.ColumnOption.SqlType

import scala.concurrent.ExecutionContext

/**
  * Created by Lance on 2016-10-12.
  */


trait AirbnbUsersDAO { self: Profile =>

  import slick.ast.BaseTypedType
  import org.joda.time.DateTime
  import io.circe.Json
  import profile.api._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table AirbnbUsers
    *  @param id Database column id SqlType(int8), PrimaryKey
    *  @param firstName Database column first_name SqlType(varchar), Length(50,true)
    *  @param about Database column about SqlType(text)
    *  @param document Database column document SqlType(jsonb), Length(2147483647,false)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz) */
  case class AirbnbUsersRow(override val id: Long, firstName: String, about: String, document: Json, createdAt: DateTime = DateTime.now(), updatedAt: DateTime = DateTime.now())
    extends DTO[Long]

  /** GetResult implicit for fetching AirbnbUsersRow objects using plain SQL queries */
  implicit def getResultAirbnbUsersRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Json], e3: GR[DateTime]): GR[AirbnbUsersRow] = GR{
    prs => import prs._
      AirbnbUsersRow.tupled((<<[Long], <<[String], <<[String], <<[Json], <<[DateTime], <<[DateTime]))
  }
  /** Table description of table airbnb_user. Objects of this class serve as prototypes for rows in queries. */
  protected class AirbnbUsersTable(_tableTag: Tag) extends Table[AirbnbUsersRow](_tableTag, "airbnb_users") with Keyed[Long] {
    def * = (id, firstName, about, document, createdAt, updatedAt) <> (AirbnbUsersRow.tupled, AirbnbUsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(firstName), Rep.Some(about), Rep.Some(document), Rep.Some(createdAt), Rep.Some(updatedAt)).shaped.<>({r=>import r._; _1.map(_=> AirbnbUsersRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int8), PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.PrimaryKey)
    /** Database column first_name SqlType(varchar), Length(50,true) */
    val firstName: Rep[String] = column[String]("first_name", O.Length(50,varying=true))
    /** Database column about SqlType(text) */
    val about: Rep[String] = column[String]("about")
    /** Database column document SqlType(jsonb), Length(2147483647,false) */
    val document: Rep[Json] = column[Json]("document")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at", SqlType("timestamp with time zone default CURRENT_TIMESTAMP"))
    /** Database column updated_at SqlType(timestamptz) */
    val updatedAt: Rep[DateTime] = column[DateTime]("updated_at", SqlType("timestamp with time zone default CURRENT_TIMESTAMP"))
  }
  /** Collection-like TableQuery object for table AirbnbUsersTable */
  lazy val AirbnbUsers = new TableQuery(tag => new AirbnbUsersTable(tag))


  object AirbnbUsersDAO extends DAO[AirbnbUsersTable, AirbnbUsersRow, Long](profile) {

    val tableQuery = AirbnbUsers
  }
}

//object AirbnbUsersDAO extends Profile with AirbnbUsersDAO with DAO[AirbnbUsersRow, Long] {
//  // Use the custom postgresql driver.
//  override val profile: MyPostgresDriver = MyPostgresDriver
//
//  import profile.api._
//
//  override val pkType = implicitly[BaseTypedType[Long]]
//  override val tableQuery = AirbnbUsers
//  override type TableType = AirbnbUsersTable
//}

//trait UserDAO { self: Profile with DatabaseComponent =>
//
//  import slick.ast.BaseTypedType
//  import org.joda.time.DateTime
//  import io.circe.Json
//  import profile.api._
//  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
//  import slick.jdbc.{GetResult => GR}
//
//  case class UserRow(override val id: Long, firstName: String, about: String, document: Json, createdAt: DateTime = DateTime.now(), updatedAt: DateTime = DateTime.now())
//    extends DTO[Long]
//
//  /** GetResult implicit for fetching AirbnbUsersRow objects using plain SQL queries */
//  implicit def getResultUserRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Json], e3: GR[DateTime]): GR[UserRow] = GR{
//    prs => import prs._
//      UserRow.tupled((<<[Long], <<[String], <<[String], <<[Json], <<[DateTime], <<[DateTime]))
//  }
//  /** Table description of table airbnb_user. Objects of this class serve as prototypes for rows in queries. */
//  protected class UserTable(_tableTag: Tag) extends Table[UserRow](_tableTag, "airbnb_users") with Keyed[Long] {
//    def * = (id, firstName, about, document, createdAt, updatedAt) <> (UserRow.tupled, UserRow.unapply)
//    /** Maps whole row to an option. Useful for outer joins. */
//    def ? = (Rep.Some(id), Rep.Some(firstName), Rep.Some(about), Rep.Some(document), Rep.Some(createdAt), Rep.Some(updatedAt)).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
//
//    /** Database column id SqlType(int8), PrimaryKey */
//    val id: Rep[Long] = column[Long]("id", O.PrimaryKey)
//    /** Database column first_name SqlType(varchar), Length(50,true) */
//    val firstName: Rep[String] = column[String]("first_name", O.Length(50,varying=true))
//    /** Database column about SqlType(text) */
//    val about: Rep[String] = column[String]("about")
//    /** Database column document SqlType(jsonb), Length(2147483647,false) */
//    val document: Rep[Json] = column[Json]("document")
//    /** Database column created_at SqlType(timestamptz) */
//    val createdAt: Rep[DateTime] = column[DateTime]("created_at", SqlType("timestamp with time zone default CURRENT_TIMESTAMP"))
//    /** Database column updated_at SqlType(timestamptz) */
//    val updatedAt: Rep[DateTime] = column[DateTime]("updated_at", SqlType("timestamp with time zone default CURRENT_TIMESTAMP"))
//  }
//  /** Collection-like TableQuery object for table AirbnbUsersTable */
//  lazy val XXs = new TableQuery(tag => new UserTable(tag))
//
//  object UserDAO extends DAO[UserTable, UserRow, Long](profile, db) {
//
//    val tableQuery = XXs
//  }
//}
