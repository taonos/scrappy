package com.airbnbData.model.query

import java.net.URL

import org.joda.time.DateTime


/**
  * Implementation independent aggregate root.
  *
  * Note that this uses Joda Time classes and UUID, which are specifically mapped
  * through the custom postgres driver.
  */
case class Property(
                     id: Long = 0L,
//                     belongsTo: Array[AirbnbUser],
                     bathrooms: Int,
                     bedrooms: Int,
                     beds: Int,
                     city: String,
                     instantBookable: Boolean,
                     isBusinessTravelReady: Boolean,
                     isNewListing: Boolean,
                     name: String,
                     personCapacity: Int,
                     propertyType: String,
                     publicAddress: String,
                     roomType: String,
                     document: String,
                     summary: String,
                     address: String,
                     description: String,
                     airbnbUrl: URL,
                     createdAt: DateTime,
                     updatedAt: Option[DateTime]
                   )

