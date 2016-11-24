package com.airbnbData.slick.repository.interpreter

import scalaz.Kleisli
import io.circe._
import io.circe.parser._
import io.circe.optics.JsonPath._
import com.airbnbData.repository.AirbnbScrapRepository
import com.airbnbData.model.{AirbnbUserCreation, Property, PropertyCreation}
import com.airbnbData.slick.dao.PropertiesDAO
import com.airbnbData.slick.dao.helper.{MyPostgresDriver, Profile}
import org.joda.time.DateTime
import play.api.libs.ws.WSClient

import scalaz.concurrent.Task

/**
  * Created by Lance on 2016-11-07.
  */



trait RequestParam[T] {
  val key: String
  val value: T

  def parameterize: (String, String) = key -> value.toString

  override def toString: String = s"($key, $value)"
}


case class ClientId(id: String = "3092nxybyb0otqw18e8nh5nty") extends RequestParam[String] {
  override val key: String = "client_id"
  override val value: String = id
}

case class Locale(v: String = "zh-CN") extends RequestParam[String] {
  override val key: String = "locale"
  override val value: String = v
}


case class Currency(v: String = "CNY") extends RequestParam[String] {
  override val key: String = "currency"
  override val value: String = v
}

// TODO: limit must be no more than 50
case class Limit(v: Int = 50) extends RequestParam[Int] {
  override val key: String = "limit"
  override val value = v
}


case class Offset(v: Int = 0) extends RequestParam[Int] {
  override val key: String = "offset"
  override val value = v
}


case class FetchFacet(v: Boolean = true) extends RequestParam[Boolean] {
  override val key: String = "fetch_facets"
  override val value = v
}

case class Guests(v: Int = 0) extends RequestParam[Int] {
  override val key: String = "guests"
  override val value = v
}

case class Ib(v: Boolean = false) extends RequestParam[Boolean] {
  override val key: String = "ib"
  override val value = v
}

case class Location(v: String = "Shanghai%2C+China") extends RequestParam[String] {
  override val key: String = "location"
  override val value: String = v
}

case class Neighborhood(v: String = "Pudong") extends RequestParam[String] {
  override val key: String = "neighborhoods%5B%5D"
  override val value: String = v
}


case class MinBathrooms(v: Int = 0) extends RequestParam[Int] {
  override val key: String = "min_bathrooms"
  override val value = v
}


case class MinBedrooms(v: Int = 0) extends RequestParam[Int] {
  override val key: String = "min_bedrooms"
  override val value = v
}


case class MinBeds(v: Int = 1) extends RequestParam[Int] {
  override val key: String = "min_beds"
  override val value = v
}


case class Sort(v: Int = 1) extends RequestParam[Int] {
  override val key: String = "sort"
  override val value = v
}

case class QueryRequest(
                         clientId: ClientId = new ClientId,
                         locale: Locale = new Locale,
                         currency: Currency = new Currency,
                         limit: Limit = new Limit,
                         offset: Offset = new Offset,
                         fetchFacets: FetchFacet = new FetchFacet,
                         guests: Guests = new Guests,
                         ib: Ib = new Ib,
                         location: Location = new Location,
                         neighborhoods: Neighborhood = new Neighborhood,
                         minBathrooms: MinBathrooms = new MinBathrooms,
                         minBedrooms: MinBedrooms = new MinBedrooms,
                         minBeds: MinBeds = new MinBeds,
                         sort: Sort = new Sort
                       ) {

  def toParamsMap: Map[String, String] = {
    Map(
      this.clientId.parameterize,
      this.locale.parameterize,
      this.currency.parameterize,
      this.limit.parameterize,
      this.offset.parameterize,
      this.fetchFacets.parameterize,
      this.guests.parameterize,
      this.ib.parameterize,
      this.location.parameterize,
      this.neighborhoods.parameterize,
      this.minBathrooms.parameterize,
      this.minBedrooms.parameterize,
      this.minBeds.parameterize,
      this.sort.parameterize
    )
  }
}

class WSAirbnbScrapRepositoryInterpreter extends AirbnbScrapRepository {

  import com.airbnbData.util.FutureOps.Implicits._

  override def scrap: Operation[List[Option[(AirbnbUserCreation, PropertyCreation)]]] = {
    Kleisli { ws =>

      val r = QueryRequest()

      // TODO: With DI, should no longer use global instance of `web service`
      val request = ws.url("https://api.airbnb.com/v2/search_results")
        //      .withRequestTimeout(7000)
        .withQueryString(
        r.clientId.parameterize,
        r.locale.parameterize,
        r.currency.parameterize,
        r.limit.parameterize,
        r.offset.parameterize,
        r.fetchFacets.parameterize,
        r.guests.parameterize,
        r.ib.parameterize,
        r.location.parameterize,
        r.neighborhoods.parameterize,
        r.minBathrooms.parameterize,
        r.minBedrooms.parameterize,
        r.minBeds.parameterize,
        r.sort.parameterize
      )

      val responseBody = request
        .get()
        .asTask

        .map(_.body)
      //      .andThen { case _ => ws.close() }
      //      .andThen { case _ => system.terminate() }


      val listOfId = responseBody
        // parse json
        .map(parse)
        // unbox json
        .map(_.getOrElse(Json.Null))
        // use optics to navigate json.
        // https://travisbrown.github.io/circe/tut/optics.html
        .map(root.search_results.each.listing.id.long.getAll)

      val fetchedAllPropertyCreations = listOfId
        .flatMap { list =>
          val listOfProperties = list
            .map { id =>
              ws
                .url("https://api.airbnb.com/v2/listings/" + id)
                .withQueryString(
                  r.clientId.parameterize,
                  r.locale.parameterize,
                  r.currency.parameterize,
                  ("_format", "v1_legacy_for_p3"),
                  ("_source", "mobile_p3"),
                  ("number_of_guests", "1")
                )
                .get()
                .asTask
                .map { response =>
                  val body = response.body
                  val json: io.circe.Json = parse(body).getOrElse(Json.Null)
                  val base = root.listing

                  // create AirbnbUserCreation
                  val userBase = root.listing.user.user
                  val airbnbUserCreation = for {
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

                  val geometryFactory = new com.vividsolutions.jts.geom.GeometryFactory(new com.vividsolutions.jts.geom.PrecisionModel())
                  // create PropertyCreation
                  val propertyCreation = for {
                    propertyType <- base.property_type.string.getOption(json)
                    publicAddress <- base.public_address.string.getOption(json)
                    roomType <- base.room_type.string.getOption(json)
                    document <- base.json.getOption(json)
                    summary <- base.summary.string.getOption(json)
                    address <- base.address.string.getOption(json)
                    description <- base.description.string.getOption(json)
                    airbnbUrl <- Some(new java.net.URL("https://www.google.com"))
                    id <- base.id.long.getOption(json)
                    bathrooms <- base.bathrooms.int.getOption(json) match {
                      case None => base.bathrooms.double.getOption(json).map(_.toInt)
                      case Some(v) => Some(v)
                    }
                    bedrooms <- base.bedrooms.int.getOption(json)
                    beds <- base.beds.int.getOption(json)
                    city <- base.city.string.getOption(json)
//                    geopoint <- for {
//                      lat <- base.lat.double.getOption(json)
//                      lng <- base.lng.double.getOption(json)
//                    } yield geometryFactory.createPoint(new com.vividsolutions.jts.geom.Coordinate(lng, lat))
                    name <- base.name.string.getOption(json)
                    personCapacity <- base.person_capacity.int.getOption(json)
                  } yield PropertyCreation(
                    id,
                    bathrooms,
                    bedrooms,
                    beds,
                    city,
                    name,
                    personCapacity,
                    propertyType,
                    publicAddress,
                    roomType,
                    document,
                    summary,
                    address,
                    description,
                    airbnbUrl
                    )

                  airbnbUserCreation.flatMap { a => propertyCreation.map((a, _)) }
                }
            }

          Task.gatherUnordered(listOfProperties)
        }
      fetchedAllPropertyCreations
    }

  }
}
