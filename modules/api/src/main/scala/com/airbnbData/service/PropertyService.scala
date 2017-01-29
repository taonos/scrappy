package com.airbnbData.service

import monix.eval.Task
import scalaz._
import monix.reactive.Observable
import slick.jdbc.JdbcBackend.DatabaseDef

/**
  * Created by Lance on 2016-10-26.
  */
trait PropertyService[Property] {
  type Dependencies = DatabaseDef
  type ObservableOp[A] = Kleisli[Observable, Dependencies, A]
  type TaskOp[A] = Kleisli[Task, Dependencies, A]

  def all(all: () => ObservableOp[Property]): TaskOp[Seq[Property]]
}
