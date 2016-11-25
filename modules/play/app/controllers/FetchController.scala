package controllers

import javax.inject.{Inject, Singleton}

import com.airbnbData.repository.{AirbnbScrapRepository, PropertyRepository, PropertyRepositoryExecutionContext}
import com.airbnbData.service.AirbnbScrapService
import slick.jdbc.JdbcBackend._
import com.airbnbData.util.TaskOps.Implicits._

import scala.concurrent.ExecutionContext
import play.api.mvc.{Action, Controller}

// TODO: How does injection work? Where to define how to inject?
@Singleton
class FetchController @Inject() (airbnbScrapService: AirbnbScrapService, airbnbScrapRepository: AirbnbScrapRepository, propertyRepo: PropertyRepository, db: Database, propertyEC: PropertyRepositoryExecutionContext) extends Controller {

  def download = Action.async {
    val client = org.http4s.client.blaze.PooledHttp1Client()

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
      .runFuture
  }
}
