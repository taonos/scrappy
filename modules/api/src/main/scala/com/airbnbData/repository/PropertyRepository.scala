package com.airbnbData.repository

import scalaz.Kleisli
import slick.jdbc.JdbcBackend.DatabaseDef
import com.airbnbData.model._
import monix.eval.Task
import monix.reactive.Observable


/**
  * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait PropertyRepository extends Repository {

  type Dependencies = DatabaseDef
  type TaskOp[A] = Kleisli[Task, Dependencies, A]
  type ObservableOp[A] = Kleisli[Observable, Dependencies, A]

  def create(property: PropertyAndAirbnbUserCreation): TaskOp[Int]

  def obv_create: (PropertyAndAirbnbUserCreation) => ObservableOp[Int] =
    create(_).mapT[Observable, Int](Observable.fromTask)

  def bulkCreate(list: Seq[PropertyAndAirbnbUserCreation]): TaskOp[Int]

  def obv_bulkCreate: (Seq[PropertyAndAirbnbUserCreation]) => ObservableOp[Int] =
    bulkCreate(_).mapT[Observable, Int](Observable.fromTask)

  def deleteAll(): TaskOp[Int]

  def close(): TaskOp[Unit]
}
