package com.airbnbData.service.interpreter


import com.airbnbData.model.{AirbnbUserCreation, PropertyCreation}
import com.airbnbData.repository.PropertyRepositoryExecutionContext
import com.airbnbData.service.AirbnbScrapService
import org.http4s.client.Client
import slick.jdbc.JdbcBackend._

import scalaz.Kleisli
import scalaz.concurrent.Task
import scalaz.syntax.traverse._
import scalaz.std.list._
import scalaz.std.option._

/**
  * Created by Lance on 2016-10-29.
  */
class AirbnbScrapServiceInterpreter extends AirbnbScrapService {
  override def scrap(guests: Int)
                    (
                      save: Seq[(AirbnbUserCreation, PropertyCreation)] => Kleisli[Task, (Database, PropertyRepositoryExecutionContext), Option[Int]],
                      scrap: Int => Kleisli[Task, Client, List[Option[(AirbnbUserCreation, PropertyCreation)]]],
                      deleteAll: () => Kleisli[Task, (Database, PropertyRepositoryExecutionContext), Int]
                    ): Kleisli[Task, (Client, Database, PropertyRepositoryExecutionContext), String] = {
    for {
      // TODO: for debug purpose only
      _ <- deleteAll().local[(Client, Database, PropertyRepositoryExecutionContext)] { case (_, d, p) => (d, p) }
      listOfUsersAndProperties <- scrap(guests).local[(Client, Database, PropertyRepositoryExecutionContext)](_._1).map { list => list.flatMap(_.toList) }
      savedResult <- save(listOfUsersAndProperties).local[(Client, Database, PropertyRepositoryExecutionContext)] { case (_, d, p) => (d, p) }
    } yield savedResult map (_.toString) getOrElse "Something is terribly wrong here!"
  }
}
