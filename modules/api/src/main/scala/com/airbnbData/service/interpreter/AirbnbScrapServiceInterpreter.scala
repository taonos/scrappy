package com.airbnbData.service.interpreter


import com.airbnbData.model.PropertyAndAirbnbUserCreation
import com.airbnbData.service.AirbnbScrapService
import monix.eval.Task
import monix.reactive.{Consumer, Observable}
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
                      save: Seq[PropertyAndAirbnbUserCreation] => Kleisli[Task, DatabaseDef, Int],
                      scrap: () => Kleisli[Task, Client, Seq[Option[PropertyAndAirbnbUserCreation]]],
                      deleteAll: () => Kleisli[Task, DatabaseDef, Int]
                    ): Operation[String] = {
    for {
      // TODO: for debug purpose only
//      _ <- deleteAll().local[Dependencies] { case (_, d) => d }
      listOfUsersAndProperties <- scrap().local[Dependencies](_._1).map { list => list.flatMap(_.toList) }
      savedResult <- save(listOfUsersAndProperties).local[Dependencies] { case (_, d) => d }
    } yield savedResult.toString
  }

  override def scrap2(
                       save: PropertyAndAirbnbUserCreation => Kleisli[Observable, DatabaseDef, Int],
                       scrap: () => Kleisli[Observable, Client, PropertyAndAirbnbUserCreation]
                     ): Kleisli[Task, (Client, DatabaseDef), Unit] = {
    Kleisli[Task, (Client, DatabaseDef), Unit] { case (ws, db) =>

      scrap()
        .run(ws)
        .mergeMap(save(_).run(db))
        .consumeWith(Consumer.foreachParallelAsync(10) { v => Task { println(v) } })
    }
//      .map { list =>
//      list.foldLeft("") { (acc, i) =>
//        i match {
//          case Some(v) => s"${v.property.id} # ${v.property.name} \n"
//          case None => acc + "Something went wrong\n"
//        }
//      }
//    }
  }
}
