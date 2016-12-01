package com.airbnbData.repository

import play.api.libs.ws.WSClient
//import org.http4s.client.Client
import scalaz.{Kleisli, OptionT}
import com.airbnbData.model.{AirbnbUserCreation, PropertyCreation}

import scalaz.concurrent.Task

/**
  * Created by Lance on 2016-10-29.
  */

trait AirbnbScrapRepository extends Repository {

  type Box[A] = Task[A]
  type Dependencies = WSClient
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap(guests: Int): Operation[List[Option[(AirbnbUserCreation, PropertyCreation)]]]
}
