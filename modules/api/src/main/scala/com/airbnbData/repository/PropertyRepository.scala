package com.airbnbData.repository

import scala.concurrent.{ExecutionContext, Future}

import com.airbnbData.model.Property

/**
  * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait PropertyRepository extends Repository {

  def lookup(id: Long)(implicit ec: PropertyRepositoryExecutionContext): Future[Option[Property]]

  def all(implicit ec: PropertyRepositoryExecutionContext): Future[Seq[Property]]

  def update(user: Property)(implicit ec: PropertyRepositoryExecutionContext): Future[Int]

  def delete(id: Long)(implicit ec: PropertyRepositoryExecutionContext): Future[Int]

  def create(user: Property)(implicit ec: PropertyRepositoryExecutionContext): Future[Int]

  def close(): Future[Unit]
}

/**
  * Type safe execution context for operations on PropertyRepo.
  */
trait PropertyRepositoryExecutionContext extends ExecutionContext