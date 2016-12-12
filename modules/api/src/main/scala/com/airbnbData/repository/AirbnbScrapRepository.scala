package com.airbnbData.repository

import com.airbnbData.model.command.PropertyAndAirbnbUserCommand
import monix.eval.Task
import monix.reactive.Observable
import play.api.libs.ws.WSClient
//import org.http4s.client.Client
import scalaz.{Kleisli}

/**
  * Created by Lance on 2016-10-29.
  */

trait AirbnbScrapRepository extends Repository {

  type Box[A] = Task[A]
  type Dependencies = WSClient
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap(): Kleisli[Observable, WSClient, PropertyAndAirbnbUserCommand]
}
