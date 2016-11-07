package controllers

import javax.inject.{Inject, Singleton}

import com.airbnbData.repository.AirbnbScrapRepository
import com.airbnbData.service.AirbnbScrapService

import scala.concurrent.ExecutionContext
import play.api.libs.ws._
import play.api.mvc.{Action, Controller}

// TODO: How does injection work? Where to define how to inject?
@Singleton
class FetchController @Inject() (ws: WSClient, airbnbScrapService: AirbnbScrapService, airbnbScrapRepository: AirbnbScrapRepository) extends Controller {

  private implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext


  def download = Action.async {
    airbnbScrapService
      .scrap((ws, airbnbScrapRepository))
      .map { result =>
        println(result)
        Ok(views.html.airbnb(result))
      }
  }
}
