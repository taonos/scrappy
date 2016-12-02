package controllers

import javax.inject.{Inject, Singleton}

import com.airbnbData.repository.{AirbnbScrapRepository, PropertyRepository, PropertyRepositoryExecutionContext}
import com.airbnbData.service.AirbnbScrapService
import slick.jdbc.JdbcBackend._
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}
// In order to evaluate tasks, we'll need a Scheduler
import monix.execution.Scheduler.Implicits.global
// A Future type that is also Cancelable
import monix.execution.CancelableFuture

// TODO: How does injection work? Where to define how to inject?
@Singleton
class FetchController @Inject() (airbnbScrapService: AirbnbScrapService, airbnbScrapRepository: AirbnbScrapRepository, propertyRepo: PropertyRepository, client: WSClient, db: Database, propertyEC: PropertyRepositoryExecutionContext) extends Controller {

  def download = Action.async {

    airbnbScrapService
      .scrap(1)(
        propertyRepo.bulkCreate,
        airbnbScrapRepository.scrap,
        propertyRepo.deleteAll
      )
      .run((client, db, propertyEC))
      .map { result =>
        println(result)
        Ok(views.html.airbnb(result))
      }
      .runAsync
  }
}
