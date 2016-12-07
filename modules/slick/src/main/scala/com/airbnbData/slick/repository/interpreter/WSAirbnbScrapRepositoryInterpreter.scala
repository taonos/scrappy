package com.airbnbData.slick.repository.interpreter

import scalaz.{Kleisli, Reader}
import io.circe._
import io.circe.parser._
import io.circe.optics.JsonPath._
import com.airbnbData.repository.AirbnbScrapRepository
import com.airbnbData.model.{AirbnbUserCreation, Property, PropertyAndAirbnbUserCreation, PropertyDetailCreation}
import monix.eval.Task
import monix.reactive.Observable
import monix.scalaz.monixToScalazMonad
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

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
    override val key: String = "_limit"
  }


  case class Offset(v: Int = 0) extends RequestParam[Int] {
    override val key: String = "_offset"
  }


  case class FetchFacet(v: Boolean = true) extends RequestParam[Boolean] {
    override val key: String = "fetch_facets"
  }

  case class Guests(v: Option[Int] = None) extends RequestParam[Option[Int]] {
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
//                           guests: Guests = new Guests,
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
                                    format: Format = new Format
                                  )
}

private object WSAirbnbScrapRepositoryInterpreter {

  private def searchUri(offset: Int = 0): Reader[WSClient, WSRequest] = {
    Reader { ws =>

      val r = RequestBuilder.QueryRequest(offset = RequestBuilder.Offset(offset))

      ws.url("https://api.airbnb.com/v2/search_results")
        //      .withRequestTimeout(7000)
        .withQueryString(
        r.clientId.parameterize,
        r.locale.parameterize,
        r.currency.parameterize,
        r.limit.parameterize,
        r.offset.parameterize,
        r.fetchFacets.parameterize,
        //        r.guests.parameterize,
        r.ib.parameterize,
        r.location.parameterize,
        //        r.neighborhoods.parameterize,
        r.minBathrooms.parameterize,
        r.minBedrooms.parameterize,
        r.minBeds.parameterize,
        r.sort.parameterize,
        "neighborhoods" -> "Pudong"
        //        "ne_lat" -> "31.41701547235951",
        //        "ne_lng" -> "121.85279846191406",
        //        "sw_lat" -> "30.9522781728166",
        //        "sw_lng" -> "121.36871337890625"
      )
    }
  }

  private def propertyUri(id: Long): Reader[WSClient, WSRequest] =
    Reader { ws =>

      val r = RequestBuilder.PropertyDetailRequest()

      ws
        .url("https://api.airbnb.com/v2/listings/" + id.toString)
        .withQueryString(
          r.clientId.parameterize,
          r.locale.parameterize,
          r.currency.parameterize,
          r.format.parameterize
          //          r.source.parameterize,
          //          r.numberOfGuests.parameterize
        )
    }
}

class WSAirbnbScrapRepositoryInterpreter extends AirbnbScrapRepository {

  import monix.execution.Scheduler.Implicits.global
  import WSAirbnbScrapRepositoryInterpreter._
  import io.circe.generic.auto._

  private case class Pagination(next_offset: Int, result_count: Int)

  private def getListOfIds(
                            acc: Seq[Long] = List(),
                            prevPagination: Option[Pagination] = Some(Pagination(-1, -1))
                          ): Kleisli[Task, WSClient, Seq[Long]] = {
    Kleisli[Task, WSClient, (Seq[Long], Option[Pagination])] { ws =>
      val searchTask: Task[WSResponse] = Task
        .fromFuture(searchUri(acc.length).run(ws).get())

      searchTask
        // get request body
        .map(_.body)
        .onErrorHandle { ex =>
          println(s"Handling error occured during list fetching!!!: $ex")
          ""
        }
        //      .andThen { case _ => ws.close() }
        //      .andThen { case _ => system.terminate() }
        // parse json
        .map(parse)
        // unbox json
        .map(_.getOrElse(Json.Null))
        // use optics to navigate json.
        // https://travisbrown.github.io/circe/tut/optics.html
        .map { json =>
          (root.search_results.each.listing.id.long.getAll(json), root.metadata.pagination.as[Pagination].getOption(json))
        }

    }
      .flatMap { case (newList, curPagination) =>
        val endReached = for {
          c <- curPagination
          p <- prevPagination
        } yield c.next_offset == p.next_offset || c.result_count == 0

        endReached match {
          case Some(v) =>
            if (v) {
              println("No more items to fetch!")
              // TODO: Implement list as Set.
              Kleisli { _ => Task { acc.distinct } }
            }
            else {
              println(s"Fetched ${newList.length} new items this run!\n$newList")
              getListOfIds(acc ++ newList, curPagination)
            }
          case None =>
            println("Something wrong with parsing pagination data!!!")
            // TODO: Implement list as Set.
            Kleisli { _ => Task { acc.distinct } }
        }
      }
  }

  private def getUserAndProp(list: Seq[Long]): Operation[List[Option[PropertyAndAirbnbUserCreation]]] = {
    Kleisli { ws =>
      val listOfProperties = list
        .map { id =>
          val propertiesTask = Task.fromFuture(
            propertyUri(id)
              .run(ws)
              .get()
            )


          propertiesTask
            .map(_.body)
            .onErrorHandle { ex =>
              println(s"Handling error occured during individual fetching!!!: $ex")
              ""
            }
            .map(PropertyAndAirbnbUserCreation.create)
        }


      Task.gatherUnordered(listOfProperties)
    }
  }

  override def scrap(): Operation[Seq[Option[PropertyAndAirbnbUserCreation]]] = {
    for {
      ids <- getListOfIds()
      list <- getUserAndProp(ids)
    } yield list
  }

  override def scrap2(): Kleisli[Observable, WSClient, Seq[Long]] = {
    Kleisli { ws =>
      Observable.fromTask(getListOfIds().run(ws))
    }
  }
}
