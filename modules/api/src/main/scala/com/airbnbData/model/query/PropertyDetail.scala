package com.airbnbData.model.query

import java.net.URL
import io.circe.Json
import org.joda.time.DateTime

case class PropertyDetail(
                     id: Long,
                     bathrooms: Int,
                     bedrooms: Int,
                     beds: Int,
                     city: String,
                     name: String,
                     personCapacity: Int,
                     propertyType: String,
                     publicAddress: String,
                     roomType: String,
                     document: Json,
                     summary: String,
                     address: String,
                     description: String,
                     airbnbUrl: URL,
                     createdAt: DateTime,
                     updatedAt: DateTime
                   )

