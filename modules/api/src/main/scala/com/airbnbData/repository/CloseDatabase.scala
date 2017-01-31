package com.airbnbData.repository

import monix.eval.Task
import slick.jdbc.JdbcBackend.DatabaseDef

import scalaz.Kleisli

/**
  * Created by Lance on 1/31/17.
  */
trait CloseDatabase {

  def close(): Kleisli[Task, DatabaseDef, Unit] =
    Kleisli { db =>
      Task { db.close() }
    }
}
