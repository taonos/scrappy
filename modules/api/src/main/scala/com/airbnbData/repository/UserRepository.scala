package com.airbnbData.repository

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

import com.airbnbData.model.User

/**
  * Created by Lance on 2016-10-26.
  */
/**
  * An implementation dependent Repository.  This could be implemented by Slick, Cassandra, or a REST API.
  */
trait UserRepository extends Repository {

  def lookup(id: UUID)(implicit ec: UserRepositoryExecutionContext): Future[Option[User]]

  def all(implicit ec: UserRepositoryExecutionContext): Future[Seq[User]]

  def update(user: User)(implicit ec: UserRepositoryExecutionContext): Future[Int]

  def delete(id: UUID)(implicit ec: UserRepositoryExecutionContext): Future[Int]

  def create(user: User)(implicit ec: UserRepositoryExecutionContext): Future[Int]

  def close(): Future[Unit]
}

/**
  * Type safe execution context for operations on UserRepo.
  */
trait UserRepositoryExecutionContext extends ExecutionContext