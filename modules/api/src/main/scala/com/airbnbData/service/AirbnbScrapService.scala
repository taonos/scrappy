package com.airbnbData.service

//import org.http4s.client.Client
import play.api.libs.ws.{WSClient => Client}
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
  type Dependencies = (Client, AirbnbScrapRepository)
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap(guests: Int)
           (
             save: Seq[(AirbnbUserCreation, PropertyCreation)] => Kleisli[Task, (Database, PropertyRepositoryExecutionContext), Option[Int]],
             scrap: Int => Kleisli[Task, Client, List[Option[(AirbnbUserCreation, PropertyCreation)]]],
             deleteAll: () => Kleisli[Task, (Database, PropertyRepositoryExecutionContext), Int]
           ): Kleisli[Task, (Client, Database, PropertyRepositoryExecutionContext), String]
}
