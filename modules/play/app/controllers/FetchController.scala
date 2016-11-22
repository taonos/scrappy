package controllers

import javax.inject.{Inject, Singleton}

import com.airbnbData.repository.{AirbnbScrapRepository, PropertyRepository, PropertyRepositoryExecutionContext}
import com.airbnbData.service.AirbnbScrapService
import slick.jdbc.JdbcBackend._
import scala.concurrent.Future

import scala.concurrent.ExecutionContext
import play.api.libs.ws._
import play.api.mvc.{Action, Controller}

// TODO: How does injection work? Where to define how to inject?
@Singleton
class FetchController @Inject() (ws: WSClient, airbnbScrapService: AirbnbScrapService, airbnbScrapRepository: AirbnbScrapRepository, propertyRepo: PropertyRepository, db: Database, propertyEC: PropertyRepositoryExecutionContext) extends Controller {

  private implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext


  def download = Action.async {
    airbnbScrapService
      .scrap(
        propertyRepo.create,
        airbnbScrapRepository.scrap _
      )
      .run((ws, db, propertyEC))
      .map { result =>
        println(result)
        Ok(views.html.airbnb(result))
      }
  }
}
