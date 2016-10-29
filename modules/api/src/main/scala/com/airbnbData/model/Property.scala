package com.airbnbData.model

import org.joda.time.DateTime
import play.api.libs.json.JsValue
import com.vividsolutions.jts.geom.Point
import java.net.URL


/**
  * Implementation independent aggregate root.
  *
  * Note that this uses Joda Time classes and UUID, which are specifically mapped
  * through the custom postgres driver.
  */
case class Property(
                     id: Long,
                     belongsTo: AirbnbUser,
                     bathrooms: Int,
                     bedrooms: Int,
                     beds: Int,
                     city: String,
                     instantBookable: Boolean,
                     isBusinessTravelReady: Boolean,
                     isNewListing: Boolean,
                     geopoint: Point,
                     name: String,
                     personCapacity: Int,
                     propertyType: String,
                     publicAddress: String,
                     roomType: String,
                     document: JsValue,
                     summary: String,
                     address: String,
                     description: String,
                     airbnbUrl: URL,
                     createdAt: DateTime,
                     updatedAt: Option[DateTime]
                   )

