package com.airbnbData.dao

import org.joda.time.DateTime
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait AirbnbUserDAO {

  def lookup(id: Long)(implicit ec: AirbnbUserDAOExecutionContext): Future[Option[AirbnbUser]]

  def all(implicit ec: AirbnbUserDAOExecutionContext): Future[Seq[AirbnbUser]]

  def update(user: AirbnbUser)(implicit ec: AirbnbUserDAOExecutionContext): Future[Int]

  def delete(id: Long)(implicit ec: AirbnbUserDAOExecutionContext): Future[Int]

  def create(user: AirbnbUser)(implicit ec: AirbnbUserDAOExecutionContext): Future[Int]

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
  * Type safe execution context for operations on AirbnbUserDAO.
  */
trait AirbnbUserDAOExecutionContext extends ExecutionContext
