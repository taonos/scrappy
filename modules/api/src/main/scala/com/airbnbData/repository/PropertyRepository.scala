package com.airbnbData.repository

import scala.concurrent.{ExecutionContext, Future}
import scalaz.Kleisli
import com.vividsolutions.jts.geom.Point
import slick.jdbc.JdbcBackend.Database
import com.airbnbData.model._
import com.airbnbData.model.{AirbnbUser, Property}

import scalaz.concurrent.Task


/**
  * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait PropertyRepository extends Repository {

  type Box[A] = Task[A]
  type Dependencies = (Database, PropertyRepositoryExecutionContext)
  type Operation[A] = Kleisli[Box, Dependencies, A]

//  def lookup(id: Long)(implicit ec: PropertyRepositoryExecutionContext): Future[Option[Property]]

//  def all(implicit ec: PropertyRepositoryExecutionContext): Future[Seq[Property]]

//  def update(user: Property)(implicit ec: PropertyRepositoryExecutionContext): Future[Int]

//  def delete(id: Long)(implicit ec: PropertyRepositoryExecutionContext): Future[Int]

  def create(user: AirbnbUserCreation, property: PropertyCreation): Operation[Int]

  def bulkCreate(list: Seq[(AirbnbUserCreation, PropertyCreation)]): Operation[Option[Int]]

  def deleteAll(): Operation[Int]

  def close(): Kleisli[Task, Database, Unit]
}

/**
  * Type safe execution context for operations on PropertyRepo.
  */
trait PropertyRepositoryExecutionContext extends ExecutionContext