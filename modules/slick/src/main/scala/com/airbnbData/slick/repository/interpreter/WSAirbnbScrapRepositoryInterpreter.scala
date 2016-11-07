package com.airbnbData.slick.repository.interpreter

import com.airbnbData.repository.AirbnbScrapRepository

import scala.concurrent.Future
import scalaz.Kleisli

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

  private implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  override def scrap: Operation[String] = {
    Kleisli[Box, Dependencies, String] { ws =>

      //    implicit val system = ActorSystem()
      //    implicit val materializer = ActorMaterializer()
      //    val ws = AhcWSClient()


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

      val response = request
        .get()
        .map(_.body)
      //      .andThen { case _ => ws.close() }
      //      .andThen { case _ => system.terminate() }

      response

    }

  }
}
