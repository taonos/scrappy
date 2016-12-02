package com.airbnbData.slick.repository.interpreter

import scalaz.{Kleisli, Reader}
import io.circe._
import io.circe.parser._
import io.circe.optics.JsonPath._
import com.airbnbData.repository.AirbnbScrapRepository
import com.airbnbData.model.{AirbnbUserCreation, Property, PropertyCreation}
import monix.eval.Task
import monix.scalaz.monixToScalazMonad
import play.api.libs.ws.{WSClient, WSRequest}

/**
  * Created by Lance on 2016-11-07.
  */

private object RequestBuilder {

  trait RequestParam[T] {
    val key: String
    val v: T

    def parameterize: (String, String) = key -> v.toString

    override def toString: String = s"($key, $v)"
  }


  case class ClientId(v: String = "3092nxybyb0otqw18e8nh5nty") extends RequestParam[String] {
    override val key: String = "client_id"
  }

  case class Locale(v: String = "zh-CN") extends RequestParam[String] {
    override val key: String = "locale"
  }


  case class Currency(v: String = "CNY") extends RequestParam[String] {
    override val key: String = "currency"
  }

  // TODO: limit must be no more than 50
  case class Limit(v: Int = 50) extends RequestParam[Int] {
    override val key: String = "limit"
  }


  case class Offset(v: Int = 0) extends RequestParam[Int] {
    override val key: String = "offset"
  }


  case class FetchFacet(v: Boolean = true) extends RequestParam[Boolean] {
    override val key: String = "fetch_facets"
  }

  case class Guests(v: Int = 0) extends RequestParam[Int] {
    override val key: String = "guests"
  }

  case class Ib(v: Boolean = false) extends RequestParam[Boolean] {
    override val key: String = "ib"
  }

  case class Location(v: String = "Shanghai%2C+China") extends RequestParam[String] {
    override val key: String = "location"
  }

  case class Neighborhood(v: String = "Pudong") extends RequestParam[String] {
    override val key: String = "neighborhoods%5B%5D"
  }


  case class MinBathrooms(v: Int = 0) extends RequestParam[Int] {
    override val key: String = "min_bathrooms"
  }


  case class MinBedrooms(v: Int = 0) extends RequestParam[Int] {
    override val key: String = "min_bedrooms"
  }


  case class MinBeds(v: Int = 1) extends RequestParam[Int] {
    override val key: String = "min_beds"
  }


  case class Sort(v: Int = 1) extends RequestParam[Int] {
    override val key: String = "sort"
  }

  // MARK: property query params
  case class Format(v: String = "v1_legacy_for_p3") extends RequestParam[String] {
    override val key: String = "_format"
  }

  case class Source(v: String = "mobile_p3") extends RequestParam[String] {
    override val key: String = "_source"
  }

  case class NumberOfGuests(v: Int) extends RequestParam[Int] {
    override val key: String = "number_of_guests"
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
                         )

  case class PropertyDetailRequest(
                                    clientId: ClientId = new ClientId,
                                    locale: Locale = new Locale,
                                    currency: Currency = new Currency,
                                    format: Format = new Format,
                                    source: Source = new Source,
                                    numberOfGuests: NumberOfGuests
                                  )
}


class WSAirbnbScrapRepositoryInterpreter extends AirbnbScrapRepository {

  import monix.execution.Scheduler.Implicits.global

  private val searchUri: Reader[WSClient, WSRequest] = {
    Reader { ws =>

      val r = RequestBuilder.QueryRequest()

      // TODO: With DI, should no longer use global instance of `web service`
      ws.url("https://api.airbnb.com/v2/search_results")
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
    }
  }

  private def propertyUri(id: Long, guests: Int): Reader[WSClient, WSRequest] =
    Reader { ws =>

      val r = RequestBuilder.PropertyDetailRequest(numberOfGuests = RequestBuilder.NumberOfGuests(guests))

      ws
        .url("https://api.airbnb.com/v2/listings/" + id.toString)
        .withQueryString(
          r.clientId.parameterize,
          r.locale.parameterize,
          r.currency.parameterize,
          r.format.parameterize,
          r.source.parameterize,
          r.numberOfGuests.parameterize
        )
    }

  private def parseAirbnbUserCreation(json: Json): Option[AirbnbUserCreation] = {
    // create AirbnbUserCreation
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

  private def parsePropertyCreation(json: Json): Option[PropertyCreation] = {
    val base = root.listing

    val geometryFactory = new com.vividsolutions.jts.geom.GeometryFactory(new com.vividsolutions.jts.geom.PrecisionModel())
    // create PropertyCreation
    for {
      propertyType <- base.property_type.string.getOption(json)
      publicAddress <- base.public_address.string.getOption(json)
      roomType <- base.room_type.string.getOption(json)
      document <- base.json.getOption(json)
      summary <- base.summary.string.getOption(json)
      address <- base.address.string.getOption(json)
      description <- base.description.string.getOption(json)
      // FIXME: Replace with proper url
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
  }

  private def getListOfIds: Kleisli[Task, WSClient, Seq[Long]] =
    Kleisli { ws =>
      val searchTask = Task.fromFuture(searchUri.run(ws).get())

      searchTask
        // get request body
        .map(_.body)
      //      .andThen { case _ => ws.close() }
      //      .andThen { case _ => system.terminate() }
        // parse json
        .map(parse)
        // unbox json
        .map(_.getOrElse(Json.Null))
        // use optics to navigate json.
        // https://travisbrown.github.io/circe/tut/optics.html
        .map(root.search_results.each.listing.id.long.getAll)
    }

  private def getUserAndProp(list: Seq[Long], guests: Int): Operation[List[Option[(AirbnbUserCreation, PropertyCreation)]]] = {
    Kleisli { ws =>
      val listOfProperties = list
        .map { id =>
          val propertiesTask = Task.fromFuture(
            propertyUri(id, guests)
              .run(ws)
              .get()
            )

          propertiesTask
            .map { response =>
              val body = response.body
              // FIXME: handle json parsing failure
              val json = parse(body).getOrElse(Json.Null)

              val airbnbUserCreation = parseAirbnbUserCreation(json)
              val propertyCreation = parsePropertyCreation(json)


              airbnbUserCreation.flatMap { a => propertyCreation.map((a, _)) }
            }
        }


      Task.gatherUnordered(listOfProperties)
    }
  }

  override def scrap(guests: Int): Operation[List[Option[(AirbnbUserCreation, PropertyCreation)]]] = {
    for {
      ids <- getListOfIds
      list <- getUserAndProp(ids, guests)
    } yield list
  }
}
