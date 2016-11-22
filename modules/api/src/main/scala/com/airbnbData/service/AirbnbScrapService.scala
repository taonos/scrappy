package com.airbnbData.service

import java.net.URL

import scala.concurrent.Future
import play.api.libs.ws._

import scalaz.Kleisli
import slick.jdbc.JdbcBackend.Database
import com.airbnbData.model._
import com.airbnbData.repository.{AirbnbScrapRepository, PropertyRepository, PropertyRepositoryExecutionContext}
import com.vividsolutions.jts.geom.Point

/**
  * Created by Lance on 2016-10-29.
  */
trait AirbnbScrapService extends Service {
  type Box[A] = Future[A]
//  type Dependencies = (WSClient, Database, AirbnbScrapRepository, PropertyRepository)
  type Dependencies = (WSClient, AirbnbScrapRepository)
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def scrap(
             save: (AirbnbUser, PropertyCreation) => Kleisli[Future, (Database, PropertyRepositoryExecutionContext), Int],
             scrap: () => Kleisli[Future, WSClient, String]
           ): Kleisli[Future, (WSClient, Database, PropertyRepositoryExecutionContext), String]
}
