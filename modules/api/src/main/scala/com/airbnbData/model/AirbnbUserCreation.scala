package com.airbnbData.model

import io.circe.Json

/**
  * Created by Lance on 2016-11-23.
  */
case class AirbnbUserCreation(id: Long, firstName: String, about: String, document: Json)

object AirbnbUserCreation {

  import io.circe._
  import io.circe.optics.JsonPath._
  import shapeless._
  import cats.data.Xor

  private[this] case class AirbnbUserCreationFields(id: Long, firstName: String, about: String)
  private[this] case class AirbnbUserCreationExtras(document: Json)

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
    } yield Generic[AirbnbUserCreation].from(
      // FIXME: IntelliJ cannot resolve the code correctly.
      Generic[AirbnbUserCreationFields].to(field) ++
        Generic[AirbnbUserCreationExtras].to(AirbnbUserCreationExtras(document))
    )
  }
}