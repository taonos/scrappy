package com.airbnbData.slick.dao.helper

/**
  * Created by Lance on 1/30/17.
  */

import scala.language.implicitConversions
import scala.concurrent.Future
import slick.basic.DatabasePublisher
import slick.dbio._
import slick.jdbc.JdbcBackend.DatabaseDef

object DBExecutor {
  object implicits {
    implicit def runOperation[R](dbOperation: DBIO[R])(implicit db: DatabaseDef): Future[R] =
      db.run(dbOperation)

    implicit def streamOperation[R, T](dbStream: StreamingDBIO[R, T])(implicit db: DatabaseDef): DatabasePublisher[T] =
      db.stream(dbStream)
  }
}
