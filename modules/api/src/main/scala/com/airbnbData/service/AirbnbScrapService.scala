package com.airbnbData.service

//import org.http4s.client.Client
import play.api.libs.ws.{WSClient => Client}

import scalaz.Kleisli
import slick.jdbc.JdbcBackend.DatabaseDef
import com.airbnbData.model.PropertyAndAirbnbUserCreation
import monix.eval.Task
import monix.reactive.Observable

/**
  * Created by Lance on 2016-10-29.
  */
trait AirbnbScrapService {
  type Box[A] = Task[A]
  type Dependencies = (Client, DatabaseDef)
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap(
             save: Seq[PropertyAndAirbnbUserCreation] => Kleisli[Task, DatabaseDef, Int],
             scrap: () => Kleisli[Task, Client, Seq[Option[PropertyAndAirbnbUserCreation]]],
             deleteAll: () => Kleisli[Task, DatabaseDef, Int]
           ): Operation[String]

  def scrap2(
    scrap2: () => Kleisli[Observable, Client, Seq[Long]]
  ): Kleisli[Observable, Client, Seq[Long]]
}
