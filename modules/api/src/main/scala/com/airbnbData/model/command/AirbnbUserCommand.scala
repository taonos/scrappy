package com.airbnbData.model.command

import io.circe.Json

/**
  * Created by Lance on 2016-11-23.
  */
case class AirbnbUserCommand(id: Long, firstName: String, about: String, document: Json)
  extends Command

object AirbnbUserCommand {

  import io.circe._
  import io.circe.optics.JsonPath._
  import shapeless.LabelledGeneric
  import shapeless.record._
  import shapeless.ops.record._
  import shapeless.syntax.singleton._

  private[this] case class AirbnbUserCreationFields(id: Long, firstName: String, about: String)

  private implicit val decodeAirbnbUserCreationFields: Decoder[AirbnbUserCreationFields] =
    Decoder.forProduct3("id", "first_name", "about")(AirbnbUserCreationFields.apply)

  // FIXME: Handle parsing error with Either.
  def fromJson(json: Json): Option[AirbnbUserCommand] = {

    val userBase = root.listing.user.user

    for {
      document <- userBase.json.getOption(json)
      field <- document.as[AirbnbUserCreationFields].fold({ _ => None }, Some(_))
    } yield LabelledGeneric[AirbnbUserCommand].from(
      // FIXME: IntelliJ cannot resolve the code correctly.
      LabelledGeneric[AirbnbUserCreationFields].to(field) +
          ('document ->> document)
    )
  }
}