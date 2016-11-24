package com.airbnbData.slick.dao

import com.airbnbData.slick.dao.helper.{PK, Profile}

/**
  * Created by Lance on 2016-10-12.
  */
trait AirbnbUsersDAO { self: Profile =>
  import org.joda.time.DateTime
  import io.circe.Json
  import profile.api._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table AirbnbUsers
    *  @param firstName Database column first_name SqlType(varchar), Length(50,true)
    *  @param about Database column about SqlType(text)
    *  @param document Database column document SqlType(jsonb), Length(2147483647,false)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None)
    *  @param id Database column id SqlType(int8), PrimaryKey */
  case class AirbnbUserRow(id: Long, firstName: String, about: String, document: Json, createdAt: DateTime, updatedAt: Option[DateTime] = None)
  //  /** GetResult implicit for fetching AirbnbUserRow objects using plain SQL queries */
  //  implicit def GetResultAirbnbUserRow(implicit e0: GR[Long], e1: GR[String], e2: GR[DateTime], e3: GR[Option[DateTime]]): GR[AirbnbUserRow] = GR{
  //    prs => import prs._
  //      AirbnbUserRow.tupled((<<[Long], <<[String], <<[String], <<[String], <<[DateTime], <<?[DateTime]))
  //  }
  /** Table description of table airbnb_user. Objects of this class serve as prototypes for rows in queries. */
  protected class AirbnbUsersTable(_tableTag: Tag) extends Table[AirbnbUserRow](_tableTag, "airbnb_users") {
    def * = (id, firstName, about, document, createdAt, updatedAt) <> (AirbnbUserRow.tupled, AirbnbUserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(firstName), Rep.Some(about), Rep.Some(document), Rep.Some(createdAt), updatedAt).shaped.<>({r=>import r._; _1.map(_=> AirbnbUserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int8), PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.PrimaryKey)
    /** Database column first_name SqlType(varchar), Length(50,true) */
    val firstName: Rep[String] = column[String]("first_name", O.Length(50,varying=true))
    /** Database column about SqlType(text) */
    val about: Rep[String] = column[String]("about")
    /** Database column document SqlType(jsonb), Length(2147483647,false) */
    val document: Rep[Json] = column[Json]("document")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("updated_at", O.Default(None))
  }
  /** Collection-like TableQuery object for table AirbnbUsersTable */
  lazy val AirbnbUsers = new TableQuery(tag => new AirbnbUsersTable(tag))
}
