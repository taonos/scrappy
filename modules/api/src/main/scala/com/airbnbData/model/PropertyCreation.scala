package com.airbnbData.model

import java.net.URL

import com.vividsolutions.jts.geom.Point
import io.circe.Json

/**
  * Created by Lance on 2016-11-22.
  */

case class PropertyCreation(
                             id: Long = 0L,
                             //                     belongsTo: Array[AirbnbUser],
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
                             airbnbUrl: URL
                           )
