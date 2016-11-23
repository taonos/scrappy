package controllers

import javax.inject.{Inject, Singleton}

import com.airbnbData.repository.{AirbnbScrapRepository, PropertyRepository, PropertyRepositoryExecutionContext}
import com.airbnbData.service.AirbnbScrapService
import slick.jdbc.JdbcBackend._
import com.airbnbData.util.TaskOps.Implicits._

import scala.concurrent.ExecutionContext
import play.api.libs.ws._
import play.api.mvc.{Action, Controller}

// TODO: How does injection work? Where to define how to inject?
@Singleton
class FetchController @Inject() (ws: WSClient, airbnbScrapService: AirbnbScrapService, airbnbScrapRepository: AirbnbScrapRepository, propertyRepo: PropertyRepository, db: Database, propertyEC: PropertyRepositoryExecutionContext) extends Controller {

  def download = Action.async {
    airbnbScrapService
      .scrap(
        propertyRepo.bulkCreate,
        airbnbScrapRepository.scrap _
      )
      .run((ws, db, propertyEC))
      .map { result =>
        println(result)
        Ok(views.html.airbnb(result))
      }
      .runFuture
  }
}
