package com.airbnbData.repository

import scala.concurrent.{ExecutionContext, Future}
import scalaz.Kleisli
import slick.jdbc.JdbcBackend.DatabaseDef
import com.airbnbData.model._
import monix.eval.Task


/**
  * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait PropertyRepository extends Repository {

  type Box[A] = Task[A]
  type Dependencies = DatabaseDef
  type Operation[A] = Kleisli[Box, Dependencies, A]

  def create(user: AirbnbUserCreation, property: PropertyCreation): Operation[Int]

  def bulkCreate(list: Seq[(AirbnbUserCreation, PropertyCreation)]): Operation[Option[Int]]

  def deleteAll(): Operation[Int]

  def close(): Kleisli[Task, DatabaseDef, Unit]
}
