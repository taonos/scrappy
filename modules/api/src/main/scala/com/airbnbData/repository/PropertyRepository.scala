package com.airbnbData.repository

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

  def create(property: PropertyAndAirbnbUserCreation): Operation[Int]

  def bulkCreate(list: Seq[PropertyAndAirbnbUserCreation]): Operation[Int]

  def deleteAll(): Operation[Int]

  def close(): Operation[Unit]
}
