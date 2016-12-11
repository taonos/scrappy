package com.airbnbData.slick.dao

import com.airbnbData.slick.dao.helper.{DTO, Profile}

/**
  * Created by Lance on 2016-10-11.
  */

trait UsersDAO { self: Profile =>
  import org.joda.time.DateTime
  import profile.api._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table UsersTable
    *  @param id Database column id SqlType(uuid), PrimaryKey
    *  @param email Database column email SqlType(varchar), Length(1024,true)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class UsersRow(id: java.util.UUID, email: String, createdAt: DateTime, updatedAt: Option[DateTime] = None)
    extends DTO
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[java.util.UUID], e1: GR[String], e2: GR[DateTime], e3: GR[Option[DateTime]]): GR[UsersRow] = GR{
    prs => import prs._
      UsersRow.tupled((<<[java.util.UUID], <<[String], <<[DateTime], <<?[DateTime]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  protected class UsersTable(_tableTag: Tag) extends Table[UsersRow](_tableTag, "users") {
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
  /** Collection-like TableQuery object for table UsersTable */
  lazy val Users = new TableQuery(tag => new UsersTable(tag))
}
