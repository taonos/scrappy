package com.airbnbData.model.command

import io.circe.Json

/**
  * Created by Lance on 2016-12-11.
  */

case class PropertyAndAirbnbUserCommand(property: PropertyDetailCommand, belongsTo: AirbnbUserCommand)
  extends Command

object PropertyAndAirbnbUserCommand {

  import io.circe.parser._

  def create(body: String): Option[PropertyAndAirbnbUserCommand] = {
    // TODO: should I pass ParsingError back as Either?
    val json = parse(body).fold[Json]({ _ => Json.Null }, identity)

    for {
      prop <- PropertyDetailCommand.fromJson(json)
      user <- AirbnbUserCommand.fromJson(json)
    } yield PropertyAndAirbnbUserCommand(prop, user)

  }
}
