package com.airbnbData.model.query

import io.circe.Json
import org.joda.time.DateTime

import scalaz.Scalaz._
import scalaz.{NonEmptyList, ValidationNel, \/}

/**
  * Implementation independent aggregate root.
  */
case class AirbnbUser(id: Long, firstName: String, about: String, document: Json, createdAt: DateTime, updatedAt: Option[DateTime])

object AirbnbUser {

  private def validateFirstName(name: String): ValidationNel[String, String] = {
    if (name.isEmpty)
      s"First name must be at least 1 character long: found $name".failureNel[String]
    else
      name.successNel[String]
  }

  private def validateAbout(about: String): ValidationNel[String, String] = {
    if (about.isEmpty)
      s"About must be at least 1 character long: found $about".failureNel[String]
    else
      about.successNel[String]
  }

  def airbnbUser(id: Long, firstName: String, about: String, document: Json, createdAt: DateTime, updatedAt: Option[DateTime]): NonEmptyList[String] \/ AirbnbUser = {
    val result = validateFirstName(firstName) |@|
      validateAbout(about)

    result { (a, b) =>
      AirbnbUser(id, a, b, document, createdAt, updatedAt)
    }.disjunction
  }
}