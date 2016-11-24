package com.airbnbData.service

import play.api.libs.ws._

import scalaz.Kleisli
import slick.jdbc.JdbcBackend.Database
import com.airbnbData.model._
import com.airbnbData.repository.{AirbnbScrapRepository, PropertyRepositoryExecutionContext}


import scalaz.concurrent.Task

/**
  * Created by Lance on 2016-10-29.
  */
trait AirbnbScrapService {
  type Box[A] = Task[A]
//  type Dependencies = (WSClient, Database, AirbnbScrapRepository, PropertyRepository)
  type Dependencies = (WSClient, AirbnbScrapRepository)
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap(
             save: Seq[(AirbnbUserCreation, PropertyCreation)] => Kleisli[Task, (Database, PropertyRepositoryExecutionContext), Option[Int]],
             scrap: () => Kleisli[Task, WSClient, List[Option[(AirbnbUserCreation, PropertyCreation)]]],
             deleteAll: () => Kleisli[Task, (Database, PropertyRepositoryExecutionContext), Int]
           ): Kleisli[Task, (WSClient, Database, PropertyRepositoryExecutionContext), String]
}
