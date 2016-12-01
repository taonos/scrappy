//package com.airbnbData.slick.repository.interpreter
//
//import scala.language.implicitConversions
//import scalaz.Kleisli
//import io.circe._
//import io.circe.parser._
//import io.circe.optics.JsonPath._
//import com.airbnbData.repository.AirbnbScrapRepository
//import com.airbnbData.model.{AirbnbUserCreation, PropertyCreation}
//import org.http4s._
//import org.http4s.QueryOps
//import org.http4s.dsl._
//
//import scalaz.concurrent.Task
//
///**
//  * Created by Lance on 2016-11-07.
//  */
//
//sealed case class ClientId(id: String = "3092nxybyb0otqw18e8nh5nty")
//object ClientId {
//  implicit val queryParamInstance = new QueryParamEncoder[ClientId] with QueryParam[ClientId] {
//    override def key = QueryParameterKey("client_id")
//
//    override def encode(value: ClientId) = QueryParameterValue(value.id.toString)
//  }
//}
//
//sealed case class Locale(v: String = "zh-CN")
//object Locale {
//  implicit val queryParamInstance = new QueryParamEncoder[Locale] with QueryParam[Locale] {
//    override def key = QueryParameterKey("locale")
//
//    override def encode(value: Locale) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//sealed case class Currency(v: String = "CNY")
//object Currency {
//  implicit val queryParamInstance = new QueryParamEncoder[Currency] with QueryParam[Currency] {
//    override def key = QueryParameterKey("currency")
//
//    override def encode(value: Currency) = QueryParameterValue(value.v.toString)
//  }
//}
//
//// TODO: limit must be no more than 50
//sealed case class Limit(v: Int = 50)
//object Limit {
//  implicit val queryParamInstance = new QueryParamEncoder[Limit] with QueryParam[Limit] {
//    override def key = QueryParameterKey("limit")
//
//    override def encode(value: Limit) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//sealed case class Offset(v: Int = 0)
//object Offset {
//  implicit val queryParamInstance = new QueryParamEncoder[Offset] with QueryParam[Offset] {
//    override def key = QueryParameterKey("offset")
//
//    override def encode(value: Offset) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//sealed case class FetchFacets(v: Boolean = true)
//object FetchFacets {
//  implicit val queryParamInstance = new QueryParamEncoder[FetchFacets] with QueryParam[FetchFacets] {
//    override def key = QueryParameterKey("fetch_facets")
//
//    override def encode(value: FetchFacets) = QueryParameterValue(value.v.toString)
//  }
//}
//
//sealed case class Guests(v: Int = 0)
//object Guests {
//  implicit val queryParamInstance = new QueryParamEncoder[Guests] with QueryParam[Guests] {
//    override def key = QueryParameterKey("guests")
//
//    override def encode(value: Guests) = QueryParameterValue(value.v.toString)
//  }
//}
//
//sealed case class Ib(v: Boolean = false)
//object Ib {
//  implicit val queryParamInstance = new QueryParamEncoder[Ib] with QueryParam[Ib] {
//    override def key = QueryParameterKey("ib")
//
//    override def encode(value: Ib) = QueryParameterValue(value.v.toString)
//  }
//}
//
//sealed case class Location(v: String = "Shanghai%2C+China")
//object Location {
//  implicit val queryParamInstance = new QueryParamEncoder[Location] with QueryParam[Location] {
//    override def key = QueryParameterKey("location")
//
//    override def encode(value: Location) = QueryParameterValue(value.v.toString)
//  }
//}
//
//sealed case class Neighborhoods(v: String = "Pudong")
//object Neighborhoods {
//  implicit val queryParamInstance = new QueryParamEncoder[Neighborhoods] with QueryParam[Neighborhoods] {
//    override def key = QueryParameterKey("neighborhoods%5B%5D")
//
//    override def encode(value: Neighborhoods) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//sealed case class MinBathrooms(v: Int = 0)
//object MinBathrooms {
//  implicit val queryParamInstance = new QueryParamEncoder[MinBathrooms] with QueryParam[MinBathrooms] {
//    override def key = QueryParameterKey("min_bathrooms")
//
//    override def encode(value: MinBathrooms) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//sealed case class MinBedrooms(v: Int = 0)
//object MinBedrooms {
//  implicit val queryParamInstance = new QueryParamEncoder[MinBedrooms] with QueryParam[MinBedrooms] {
//    override def key = QueryParameterKey("min_bedrooms")
//
//    override def encode(value: MinBedrooms) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//sealed case class MinBeds(v: Int = 1)
//object MinBeds {
//  implicit val queryParamInstance = new QueryParamEncoder[MinBeds] with QueryParam[MinBeds] {
//    override def key = QueryParameterKey("min_beds")
//
//    override def encode(value: MinBeds) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//sealed case class Sort(v: Int = 1)
//object Sort {
//  implicit val queryParamInstance = new QueryParamEncoder[Sort] with QueryParam[Sort] {
//    override def key = QueryParameterKey("sort")
//
//    override def encode(value: Sort) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//
//// MARK: property query params
//
//sealed case class Format(v: String = "v1_legacy_for_p3")
//object Format {
//  implicit val queryParamInstance = new QueryParamEncoder[Format] with QueryParam[Format] {
//    override def key = QueryParameterKey("_format")
//
//    override def encode(value: Format) = QueryParameterValue(value.v.toString)
//  }
//}
//
//sealed case class Source(v: String = "mobile_p3")
//object Source {
//  implicit val queryParamInstance = new QueryParamEncoder[Source] with QueryParam[Source] {
//    override def key = QueryParameterKey("_source")
//
//    override def encode(value: Source) = QueryParameterValue(value.v.toString)
//  }
//}
//
//sealed case class NumberOfGuests(v: Int)
//object NumberOfGuests {
//  implicit val queryParamInstance = new QueryParamEncoder[NumberOfGuests] with QueryParam[NumberOfGuests] {
//    override def key = QueryParameterKey("number_of_guests")
//
//    override def encode(value: NumberOfGuests) = QueryParameterValue(value.v.toString)
//  }
//}
//
//
//
//class Http4sAirbnbScrapRepositoryInterpreter extends AirbnbScrapRepository {
//
//
//  private def getUri(uri: String) =
//  // TODO: what's with the error???
//    Uri.fromString(uri).fold(_ => sys.error(s"Failure on uri: $uri"), identity)
//
//  private def searchUri: Uri = {
//    getUri("https://api.airbnb.com/v2/search_results")
//      .+*?(ClientId())
//      .+*?(Locale())
//      .+*?(Currency())
//      .+*?(Limit())
//      .+*?(Offset())
//      .+*?(FetchFacets())
//      .+*?(Guests())
//      .+*?(Ib())
//      .+*?(Location())
//      .+*?(Neighborhoods())
//      .+*?(MinBathrooms())
//      .+*?(MinBedrooms())
//      .+*?(MinBeds())
//      .+*?(Sort())
//  }
//
//  private def propertyUri(guests: Int): Uri = {
//    getUri("https://api.airbnb.com/v2/listings")
//      .+*?(ClientId())
//      .+*?(Locale())
//      .+*?(Currency())
//      .+*?(Format())
//      .+*?(Source())
//      .+*?(NumberOfGuests(guests))
//  }
//
//  private def parseAirbnbUserCreation(json: Json): Option[AirbnbUserCreation] = {
//    // create AirbnbUserCreation
//    val userBase = root.listing.user.user
//
//    for {
//      id <- userBase.id.long.getOption(json)
//      firstName <- userBase.first_name.string.getOption(json)
//      about <- userBase.about.string.getOption(json)
//      document <- root.listing.json.getOption(json)
//    } yield AirbnbUserCreation(
//      id,
//      firstName,
//      about,
//      document
//    )
//  }
//
//  private def parsePropertyCreation(json: Json): Option[PropertyCreation] = {
//    val base = root.listing
//
//    val geometryFactory = new com.vividsolutions.jts.geom.GeometryFactory(new com.vividsolutions.jts.geom.PrecisionModel())
//    // create PropertyCreation
//    for {
//      propertyType <- base.property_type.string.getOption(json)
//      publicAddress <- base.public_address.string.getOption(json)
//      roomType <- base.room_type.string.getOption(json)
//      document <- base.json.getOption(json)
//      summary <- base.summary.string.getOption(json)
//      address <- base.address.string.getOption(json)
//      description <- base.description.string.getOption(json)
//      // FIXME: Replace with proper url
//      airbnbUrl <- Some(new java.net.URL("https://www.google.com"))
//      id <- base.id.long.getOption(json)
//      bathrooms <- base.bathrooms.int.getOption(json) match {
//        case None => base.bathrooms.double.getOption(json).map(_.toInt)
//        case Some(v) => Some(v)
//      }
//      bedrooms <- base.bedrooms.int.getOption(json)
//      beds <- base.beds.int.getOption(json)
//      city <- base.city.string.getOption(json)
//      //                    geopoint <- for {
//      //                      lat <- base.lat.double.getOption(json)
//      //                      lng <- base.lng.double.getOption(json)
//      //                    } yield geometryFactory.createPoint(new com.vividsolutions.jts.geom.Coordinate(lng, lat))
//      name <- base.name.string.getOption(json)
//      personCapacity <- base.person_capacity.int.getOption(json)
//    } yield PropertyCreation(
//      id,
//      bathrooms,
//      bedrooms,
//      beds,
//      city,
//      name,
//      personCapacity,
//      propertyType,
//      publicAddress,
//      roomType,
//      document,
//      summary,
//      address,
//      description,
//      airbnbUrl
//    )
//  }
//
//  def scrap(guests: Int): Kleisli[Task, org.http4s.client.Client, List[Option[(AirbnbUserCreation, PropertyCreation)]]] = {
//    Kleisli { client =>
//
//      val request = searchUri
//
//      val response = client.expect[String](request)
//
//      val listOfId = response
//        // parse json with circe
//        .map(parse)
//        // FIXME: handle json parsing failure
//        .map(_.getOrElse(Json.Null))
//        // use optics to navigate json.
//        // https://travisbrown.github.io/circe/tut/optics.html
//        .map(root.search_results.each.listing.id.long.getAll)
//
//      listOfId
//        .flatMap { list =>
//          val listOfProperties = list
//            .map { id =>
//              val propertyReq = propertyUri(guests) / id.toString
//              val response = client.expect[String](propertyReq)
//
//              response
//                .map { property =>
//                  // FIXME: handle json parsing failure
//                  val json = parse(property).getOrElse(Json.Null)
//
//                  val airbnbUserCreation = parseAirbnbUserCreation(json)
//                  val propertyCreation = parsePropertyCreation(json)
//
//                  airbnbUserCreation.flatMap { a => propertyCreation.map((a, _)) }
//                }
//            }
//
//          Task.gatherUnordered(listOfProperties)
//        }
//    }
//
//  }
//}
