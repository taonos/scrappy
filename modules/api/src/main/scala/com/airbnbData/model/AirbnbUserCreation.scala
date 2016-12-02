package com.airbnbData.model

import io.circe.Json

/**
  * Created by Lance on 2016-11-23.
  */
case class AirbnbUserCreation(id: Long, firstName: String, about: String, document: Json)

object AirbnbUserCreation {

  import io.circe._
  import io.circe.optics.JsonPath._

  def fromJson(json: Json): Option[AirbnbUserCreation] = {
    val userBase = root.listing.user.user

    for {
      id <- userBase.id.long.getOption(json)
      firstName <- userBase.first_name.string.getOption(json)
      about <- userBase.about.string.getOption(json)
      document <- root.listing.json.getOption(json)
    } yield AirbnbUserCreation(
      id,
      firstName,
      about,
      document
    )
  }
}