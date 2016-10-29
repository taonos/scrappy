package com.airbnbData.repository

import scala.concurrent.{ExecutionContext, Future}

import scalaz._

import com.airbnbData.model.AirbnbUser

/**
  * An implementation dependent Repository.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait AirbnbUserRepository extends Repository {

  def all: Future[\/[NonEmptyList[String], Seq[AirbnbUser]]]



  def lookup(id: Long)(implicit ec: AirbnbUserRepositoryExecutionContext): Future[Option[AirbnbUser]]

  def all(implicit ec: AirbnbUserRepositoryExecutionContext): Future[Seq[AirbnbUser]]

  def update(user: AirbnbUser)(implicit ec: AirbnbUserRepositoryExecutionContext): Future[Int]

  def delete(id: Long)(implicit ec: AirbnbUserRepositoryExecutionContext): Future[Int]

  def create(user: AirbnbUser)(implicit ec: AirbnbUserRepositoryExecutionContext): Future[Int]

  def close(): Future[Unit]
}

/**
  * Type safe execution context for operations on AirbnbUserRepo.
  */
trait AirbnbUserRepositoryExecutionContext extends ExecutionContext

