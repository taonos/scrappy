package com.airbnbData.slick.dao

import com.airbnbData.slick.dao.helper.{PK, Profile}
import slick.model

/**
  * Created by Lance on 2016-10-12.
  */
trait AirbnbUserPropertiesDAO extends AirbnbUsersDAO with PropertiesDAO { self: Profile =>
  import org.joda.time.DateTime
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table AirbnbUserPropertiesTable
    *  @param userId Database column user_id SqlType(int8)
    *  @param propertyId Database column property_id SqlType(int8)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz), Default(None) */
  case class AirbnbUserPropertyRow(userId: PK[AirbnbUsersTable], propertyId: Long, createdAt: DateTime, updatedAt: Option[DateTime] = None)
  //  /** GetResult implicit for fetching UserPropertyRow objects using plain SQL queries */
  //  implicit def GetResultUserPropertyRow(implicit e0: GR[Long], e1: GR[DateTime], e2: GR[Option[DateTime]]): GR[UserPropertyRow] = GR{
  //    prs => import prs._
  //      UserPropertyRow.tupled((<<[Long], <<[Long], <<[DateTime], <<?[DateTime]))
  //  }
  /** Table description of table airbnb_user_properties. Objects of this class serve as prototypes for rows in queries. */
  protected class AirbnbUserPropertiesTable(_tableTag: Tag) extends Table[AirbnbUserPropertyRow](_tableTag, "user_property") {
    def * = (userId, propertyId, createdAt, updatedAt) <> (AirbnbUserPropertyRow.tupled, AirbnbUserPropertyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(propertyId), Rep.Some(createdAt), updatedAt).shaped.<>({r=>import r._; _1.map(_=> AirbnbUserPropertyRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(int8) */
    val userId: Rep[PK[AirbnbUsersTable]] = column[PK[AirbnbUsersTable]]("user_id")
    /** Database column property_id SqlType(int8) */
    val propertyId: Rep[Long] = column[Long]("property_id")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("updated_at", O.Default(None))

    /** Primary key of UserProperty (database name user_property_pkey) */
    val pk = primaryKey("user_property_pkey", (propertyId, userId))

    /** Foreign key referencing AirbnbUser (database name user_id_FK) */
    lazy val airbnbUserFk = foreignKey("user_id_FK", userId, AirbnbUsers)(r => r.id, onUpdate=model.ForeignKeyAction.NoAction, onDelete=model.ForeignKeyAction.Restrict)
    /** Foreign key referencing Property (database name property_id_FK) */
    lazy val propertyFk = foreignKey("property_id_FK", propertyId, Properties)(r => r.id, onUpdate=model.ForeignKeyAction.NoAction, onDelete=model.ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table AirbnbUserPropertiesTable */
  lazy val AirbnbUserProperties = new TableQuery(tag => new AirbnbUserPropertiesTable(tag))

}
