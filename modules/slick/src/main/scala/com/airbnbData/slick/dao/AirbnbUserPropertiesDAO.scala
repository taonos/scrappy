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
  import slick.sql.SqlProfile.ColumnOption.SqlType

  /** Entity class storing rows of table AirbnbUserPropertiesTable
    *  @param userId Database column user_id SqlType(int8)
    *  @param propertyId Database column property_id SqlType(int8)
    *  @param createdAt Database column created_at SqlType(timestamptz)
    *  @param updatedAt Database column updated_at SqlType(timestamptz) */
  case class AirbnbUserPropertiesRow(userId: Long, propertyId: Long, createdAt: DateTime = DateTime.now(), updatedAt: DateTime = DateTime.now())
  /** GetResult implicit for fetching AirbnbUserPropertiesRow objects using plain SQL queries */
  implicit def GetResultAirbnbUserPropertiesRow(implicit e0: GR[Long], e1: GR[DateTime]): GR[AirbnbUserPropertiesRow] = GR{
    prs => import prs._
      AirbnbUserPropertiesRow.tupled((<<[Long], <<[Long], <<[DateTime], <<[DateTime]))
  }
  /** Table description of table airbnb_user_properties. Objects of this class serve as prototypes for rows in queries. */
  protected class AirbnbUserPropertiesTable(_tableTag: Tag) extends Table[AirbnbUserPropertiesRow](_tableTag, "airbnb_user_properties") {
    def * = (airbnbUserId, propertyId, createdAt, updatedAt) <> (AirbnbUserPropertiesRow.tupled, AirbnbUserPropertiesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(airbnbUserId), Rep.Some(propertyId), Rep.Some(createdAt), Rep.Some(updatedAt)).shaped.<>({ r=>import r._; _1.map(_=> AirbnbUserPropertiesRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(int8) */
    val airbnbUserId: Rep[Long] = column[Long]("airbnb_user_id")
    /** Database column property_id SqlType(int8) */
    val propertyId: Rep[Long] = column[Long]("property_id")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at", SqlType("timestamp with time zone default CURRENT_TIMESTAMP"))
    /** Database column updated_at SqlType(timestamptz) */
    val updatedAt: Rep[DateTime] = column[DateTime]("updated_at", SqlType("timestamp with time zone default CURRENT_TIMESTAMP"))

    /** Primary key of UserProperty (database name user_property_pkey) */
    val pk = primaryKey("user_property_pkey", (propertyId, airbnbUserId))

    /** Foreign key referencing AirbnbUser (database name user_id_FK) */
    lazy val airbnbUserFk = foreignKey("user_id_FK", airbnbUserId, AirbnbUsers)(r => r.id, onUpdate=model.ForeignKeyAction.NoAction, onDelete=model.ForeignKeyAction.Restrict)
    /** Foreign key referencing Property (database name property_id_FK) */
    lazy val propertyFk = foreignKey("property_id_FK", propertyId, Properties)(r => r.id, onUpdate=model.ForeignKeyAction.NoAction, onDelete=model.ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table AirbnbUserPropertiesTable */
  lazy val AirbnbUserProperties = new TableQuery(tag => new AirbnbUserPropertiesTable(tag))

}
