package com.airbnbData.service.interpreter


import com.airbnbData.service.AirbnbScrapService

import scalaz.Kleisli

/**
  * Created by Lance on 2016-10-29.
  */
class AirbnbScrapServiceInterpreter extends AirbnbScrapService {
  override def scrap: Operation[String] = {
    Kleisli {
      case (ws, repo) => {
        repo.scrap.run(ws)
      }
    }
  }
}
