package com.airbnbData.repository

import scalaz.{Kleisli, OptionT}
import org.http4s.client.Client
import com.airbnbData.model.{AirbnbUserCreation, PropertyCreation}

import scalaz.concurrent.Task

/**
  * Created by Lance on 2016-10-29.
  */

trait AirbnbScrapRepository extends Repository {

  type Box[A] = Task[A]
  type Dependencies = Client
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap(guests: Int): Operation[List[Option[(AirbnbUserCreation, PropertyCreation)]]]
}
