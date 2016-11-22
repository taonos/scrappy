package com.airbnbData.service.interpreter


import com.airbnbData.model.{AirbnbUser, PropertyCreation}
import com.airbnbData.repository.PropertyRepositoryExecutionContext
import com.airbnbData.service.AirbnbScrapService
import play.api.libs.ws.WSClient
import slick.jdbc.JdbcBackend._

import scala.concurrent.Future
import scalaz.Kleisli

/**
  * Created by Lance on 2016-10-29.
  */
class AirbnbScrapServiceInterpreter extends AirbnbScrapService {
  override def scrap(
                      save: (AirbnbUser, PropertyCreation) => Kleisli[Future, (Database, PropertyRepositoryExecutionContext), Int],
                      scrap: () => Kleisli[Future, WSClient, String]
                    ): Kleisli[Future, (WSClient, Database, PropertyRepositoryExecutionContext), String] = {
    Kleisli { case (ws, db, ec) =>
      scrap().run(ws)
    }
  }
}
