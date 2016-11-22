package com.airbnbData.repository

import scala.concurrent.{Future}

import scalaz.Kleisli
import play.api.libs.ws._

import com.airbnbData.model

/**
  * Created by Lance on 2016-10-29.
  */

trait AirbnbScrapRepository extends Repository {

  type Box[A] = Future[A]
  type Dependencies = WSClient
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap: Kleisli[Future, WSClient, String]
}
