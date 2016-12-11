package com.airbnbData.model

import io.circe.Json

/**
  * Created by Lance on 2016-11-23.
  */
case class AirbnbUserCreation(id: Long, firstName: String, about: String, document: Json)
  extends CommandModel

object AirbnbUserCreation {

  import io.circe._
  import io.circe.optics.JsonPath._
  import cats.data.Xor

  import shapeless._
  import shapeless.record._
  import shapeless.ops.record._
  import shapeless.syntax.singleton._

  private[this] case class AirbnbUserCreationFields(id: Long, firstName: String, about: String)

  private implicit val decodeAirbnbUserCreationFields: Decoder[AirbnbUserCreationFields] =
    Decoder.forProduct3("id", "first_name", "about")(AirbnbUserCreationFields.apply)

  // FIXME: Handle parsing error with Either.
  def fromJson(json: Json): Option[AirbnbUserCreation] = {

    val userBase = root.listing.user.user

    for {
      document <- userBase.json.getOption(json)
      field <- document.as[AirbnbUserCreationFields] match {
        case Xor.Left(_) => None
        case Xor.Right(v) => Some(v)
      }
    } yield LabelledGeneric[AirbnbUserCreation].from(
      // FIXME: IntelliJ cannot resolve the code correctly.
      LabelledGeneric[AirbnbUserCreationFields].to(field) +
          ('document ->> document)
    )
  }
}