package com.airbnbData.service.interpreter


import com.airbnbData.model.{AirbnbUserCreation, PropertyCreation}
import com.airbnbData.repository.PropertyRepositoryExecutionContext
import com.airbnbData.service.AirbnbScrapService
import play.api.libs.ws.WSClient
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
  override def scrap(
                      save: Seq[(AirbnbUserCreation, PropertyCreation)] => Kleisli[Task, (Database, PropertyRepositoryExecutionContext), Option[Int]],
                      scrap: () => Kleisli[Task, WSClient, List[Option[(AirbnbUserCreation, PropertyCreation)]]]
                    ): Kleisli[Task, (WSClient, Database, PropertyRepositoryExecutionContext), String] = {
    for {
      listOfUsersAndProperties <- scrap().local[(WSClient, Database, PropertyRepositoryExecutionContext)](_._1).map { list => list.flatMap(_.toList) }
      savedResult <- save(listOfUsersAndProperties).local[(WSClient, Database, PropertyRepositoryExecutionContext)] { case (_, d, p) => (d, p) }
    } yield savedResult map (_.toString) getOrElse "Something is terribly wrong here!"
  }
}
