package com.airbnbData.service.interpreter


import com.airbnbData.model.{AirbnbUserCreation, PropertyCreation}
import com.airbnbData.service.AirbnbScrapService
import monix.eval.Task
import monix.reactive.Observable
import monix.scalaz.monixToScalazMonad
//import org.http4s.client.Client
import play.api.libs.ws.{WSClient => Client}
import slick.jdbc.JdbcBackend._
import scalaz.Kleisli

/**
  * Created by Lance on 2016-10-29.
  */
class AirbnbScrapServiceInterpreter extends AirbnbScrapService {
  override def scrap(
                      save: Seq[(AirbnbUserCreation, PropertyCreation)] => Kleisli[Task, DatabaseDef, Option[Int]],
                      scrap: () => Kleisli[Task, Client, Seq[Option[(AirbnbUserCreation, PropertyCreation)]]],
                      deleteAll: () => Kleisli[Task, DatabaseDef, Int]
                    ): Operation[String] = {
    for {
      // TODO: for debug purpose only
      _ <- deleteAll().local[Dependencies] { case (_, d) => d }
      listOfUsersAndProperties <- scrap().local[Dependencies](_._1).map { list => list.flatMap(_.toList) }
      savedResult <- save(listOfUsersAndProperties).local[Dependencies] { case (_, d) => d }
    } yield savedResult map (_.toString) getOrElse "Something is terribly wrong here!"
  }

  override def scrap2(
                     scrap2: () => Kleisli[Observable, Client, Seq[Long]]
  ): Kleisli[Observable, Client, Seq[Long]] = {
    scrap2()
  }
}
