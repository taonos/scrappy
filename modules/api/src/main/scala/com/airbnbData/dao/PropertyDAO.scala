package com.airbnbData.dao

import org.joda.time.DateTime
import play.api.libs.json.JsValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait PropertyDAO {

  def lookup(id: Long)(implicit ec: PropertyDAOExecutionContext): Future[Option[Property]]

  def all(implicit ec: PropertyDAOExecutionContext): Future[Seq[Property]]

  def update(user: Property)(implicit ec: PropertyDAOExecutionContext): Future[Int]

  def delete(id: Long)(implicit ec: PropertyDAOExecutionContext): Future[Int]

  def create(user: Property)(implicit ec: PropertyDAOExecutionContext): Future[Int]

  def close(): Future[Unit]
}

/**
  * Implementation independent aggregate root.
  *
  * Note that this uses Joda Time classes and UUID, which are specifically mapped
  * through the custom postgres driver.
  */
case class Property(id: Long, firstName: String, about: String, document: JsValue, createdAt: DateTime, updatedAt: Option[DateTime])

/**
  * Type safe execution context for operations on PropertyDAO.
  */
trait PropertyDAOExecutionContext extends ExecutionContext
