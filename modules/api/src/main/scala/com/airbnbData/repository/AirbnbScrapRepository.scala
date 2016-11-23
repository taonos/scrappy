package com.airbnbData.repository

import scala.concurrent.Future
import scalaz.{Kleisli, OptionT}
import play.api.libs.ws._
import com.airbnbData.model
import com.airbnbData.model.{AirbnbUserCreation, PropertyCreation}

import scalaz.concurrent.Task

/**
  * Created by Lance on 2016-10-29.
  */

trait AirbnbScrapRepository extends Repository {

  type Box[A] = Task[A]
  type Dependencies = WSClient
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap: Operation[List[Option[(AirbnbUserCreation, PropertyCreation)]]]
}
