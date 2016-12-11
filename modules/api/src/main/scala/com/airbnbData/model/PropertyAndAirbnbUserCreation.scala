package com.airbnbData.model

import io.circe.Json

/**
  * Created by Lance on 2016-12-11.
  */

case class PropertyAndAirbnbUserCreation(property: PropertyDetailCreation, belongsTo: AirbnbUserCreation)
  extends CommandModel

object PropertyAndAirbnbUserCreation {

  import io.circe.parser._

  def create(body: String): Option[PropertyAndAirbnbUserCreation] = {
    // TODO: should I pass ParsingError back as Either?
    val json = parse(body).getOrElse(Json.Null)

    for {
      prop <- PropertyDetailCreation.fromJson(json)
      user <- AirbnbUserCreation.fromJson(json)
    } yield PropertyAndAirbnbUserCreation(prop, user)

  }
}
