package com.airbnbData.service.interpreter


import com.airbnbData.model.{AirbnbUser, PropertyCreation}
import com.airbnbData.repository.PropertyRepositoryExecutionContext
import com.airbnbData.service.AirbnbScrapService
import play.api.libs.ws.WSClient
import slick.jdbc.JdbcBackend._

import scalaz.Kleisli
import scalaz.concurrent.Task

/**
  * Created by Lance on 2016-10-29.
  */
class AirbnbScrapServiceInterpreter extends AirbnbScrapService {
  override def scrap(
                      save: (AirbnbUser, PropertyCreation) => Kleisli[Task, (Database, PropertyRepositoryExecutionContext), Int],
                      scrap: () => Kleisli[Task, WSClient, String]
                    ): Kleisli[Task, (WSClient, Database, PropertyRepositoryExecutionContext), String] = {
    Kleisli { case (ws, db, ec) =>
      scrap().run(ws)
    }
  }
}
