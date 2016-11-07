package com.airbnbData.service

import scala.concurrent.{Future}
import play.api.libs.ws._
import scalaz.Kleisli

import com.airbnbData.repository.{AirbnbScrapRepository}

/**
  * Created by Lance on 2016-10-29.
  */
trait AirbnbScrapService extends Service {
  type Box[A] = Future[A]
  type Dependencies = (WSClient, AirbnbScrapRepository)
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap: Operation[String]
}
