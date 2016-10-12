package com.airbnbData.model

import java.util.UUID

import org.joda.time.{DateTime, Instant}

import scala.concurrent.{ExecutionContext, Future}

/**
 * An implementation dependent Repository.  This could be implemented by Slick, Cassandra, or a REST API.
 */
trait UserRepo extends Repository {

  def lookup(id: UUID)(implicit ec: UserRepoExecutionContext): Future[Option[User]]

  def all(implicit ec: UserRepoExecutionContext): Future[Seq[User]]

  def update(user: User)(implicit ec: UserRepoExecutionContext): Future[Int]

  def delete(id: UUID)(implicit ec: UserRepoExecutionContext): Future[Int]

  def create(user: User)(implicit ec: UserRepoExecutionContext): Future[Int]

  def close(): Future[Unit]
}

/**
 * Implementation independent aggregate root.
 *
 * Note that this uses Joda Time classes and UUID, which are specifically mapped
 * through the custom postgres driver.
 */
case class User(id: UUID, email: String, createdAt: DateTime, updatedAt: Option[DateTime])

/**
 * Type safe execution context for operations on UserRepo.
 */
trait UserRepoExecutionContext extends ExecutionContext
