package com.airbnbData.model

import org.joda.time.DateTime
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * An implementation dependent Repository.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait AirbnbUserRepo extends Repository {

  def lookup(id: Long)(implicit ec: AirbnbUserRepoExecutionContext): Future[Option[AirbnbUser]]

  def all(implicit ec: AirbnbUserRepoExecutionContext): Future[Seq[AirbnbUser]]

  def update(user: AirbnbUser)(implicit ec: AirbnbUserRepoExecutionContext): Future[Int]

  def delete(id: Long)(implicit ec: AirbnbUserRepoExecutionContext): Future[Int]

  def create(user: AirbnbUser)(implicit ec: AirbnbUserRepoExecutionContext): Future[Int]

  def close(): Future[Unit]
}

/**
  * Implementation independent aggregate root.
  *
  * Note that this uses Joda Time classes and UUID, which are specifically mapped
  * through the custom postgres driver.
  */
case class AirbnbUser(id: Long, firstName: String, about: String, document: JsValue, createdAt: DateTime, updatedAt: Option[DateTime])

/**
  * Type safe execution context for operations on AirbnbUserRepo.
  */
trait AirbnbUserRepoExecutionContext extends ExecutionContext
