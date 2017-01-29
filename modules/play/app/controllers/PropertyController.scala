package controllers

import javax.inject.{Inject, Singleton}

import com.airbnbData.model.query.Property
import com.airbnbData.repository.PropertyRepository
import com.airbnbData.service.PropertyService
import play.api.mvc._
import slick.jdbc.JdbcBackend.DatabaseDef

@Singleton
class PropertyController @Inject() (propertyService: PropertyService[Property], propertyRepo: PropertyRepository, db: DatabaseDef) extends Controller {

  import monix.execution.Scheduler.Implicits.global

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def index = Action.async {
    logger.info("Calling index")

    propertyService
      .all(propertyRepo.all)
      .run(db)
      .map { properties =>
        println(properties)
        logger.info(s"Calling index: properties = ${properties}")
        Ok(views.html.properties(properties))
      }

      .runAsync

  }

}
