package com.airbnbData.model.slick.dao

import java.net.URL

/**
  * Created by Lance on 2016-10-12.
  */
trait PropertiesDAO { self: Profile =>
  import com.vividsolutions.jts.geom.Point
  import org.joda.time.DateTime
  import play.api.libs.json.JsValue
  import profile.api._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** Entity class storing rows of table Properties
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
  case class PropertyRow(id: Long, bathrooms: Int = 0, bedrooms: Int = 0, beds: Int = 0, city: String, instantBookable: Boolean = false, isBusinessTravelReady: Boolean = false, isNewListing: Boolean = false, geopoint: Point, name: String, personCapacity: Int = 0, propertyType: String, publicAddress: String, roomType: String, document: JsValue, summary: String, address: String, description: String, airbnbUrl: URL, createdAt: DateTime, updatedAt: Option[DateTime] = None)
  //  /** GetResult implicit for fetching PropertyRow objects using plain SQL queries */
  //  implicit def GetResultPropertyRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[String], e3: GR[Boolean], e4: GR[DateTime], e5: GR[Option[DateTime]]): GR[PropertyRow] = GR{
  //    prs => import prs._
  //      PropertyRow.tupled((<<[Long], <<[Int], <<[Int], <<[Int], <<[String], <<[Boolean], <<[Boolean], <<[Boolean], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[DateTime], <<?[DateTime]))
  //  }
  /** Table description of table properties. Objects of this class serve as prototypes for rows in queries. */
  protected class PropertiesTable(_tableTag: Tag) extends Table[PropertyRow](_tableTag, "property") {
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
    val airbnbUrl: Rep[URL] = column[URL]("airbnb_url", O.Length(2038,varying=true))
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[DateTime] = column[DateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz), Default(None) */
    val updatedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("updated_at", O.Default(None))
  }
  /** Collection-like TableQuery object for table PropertyTable */
  lazy val Properties = new TableQuery(tag => new PropertiesTable(tag))

}
