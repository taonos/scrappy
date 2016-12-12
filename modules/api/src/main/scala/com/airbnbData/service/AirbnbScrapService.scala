package com.airbnbData.service

//import org.http4s.client.Client
import com.airbnbData.model.command.PropertyAndAirbnbUserCommand
import play.api.libs.ws.{WSClient => Client}

import scalaz.Kleisli
import slick.jdbc.JdbcBackend.DatabaseDef
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
             save: Seq[PropertyAndAirbnbUserCommand] => Kleisli[Task, DatabaseDef, Int],
             scrap: () => Kleisli[Task, Client, Seq[Option[PropertyAndAirbnbUserCommand]]],
             deleteAll: () => Kleisli[Task, DatabaseDef, Int]
           ): Operation[String]

  def scrap2(
              save: PropertyAndAirbnbUserCommand => Kleisli[Observable, DatabaseDef, Int],
              scrap: () => Kleisli[Observable, Client, PropertyAndAirbnbUserCommand]
            ): Kleisli[Task, (Client, DatabaseDef), Unit]
}
