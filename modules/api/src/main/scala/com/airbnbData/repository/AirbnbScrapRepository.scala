package com.airbnbData.repository

import monix.eval.Task
import monix.reactive.Observable
import play.api.libs.ws.WSClient
//import org.http4s.client.Client
import scalaz.{Kleisli, OptionT}
import com.airbnbData.model.{PropertyAndAirbnbUserCreation}

/**
  * Created by Lance on 2016-10-29.
  */

trait AirbnbScrapRepository extends Repository {

  type Box[A] = Task[A]
  type Dependencies = WSClient
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap(): Operation[Seq[Option[PropertyAndAirbnbUserCreation]]]
  def scrap2(): Kleisli[Observable, Dependencies, Seq[Long]]
}
