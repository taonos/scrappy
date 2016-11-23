package com.airbnbData.model

import io.circe.Json

/**
  * Created by Lance on 2016-11-23.
  */
case class AirbnbUserCreation(id: Long, firstName: String, about: String, document: Json)

//object AirbnbUserCreation {
//
//}