package com.airbnbData.service

import scala.concurrent.{ExecutionContext, Future}
import scalaz._

import com.airbnbData.repository.{UserRepository, UserRepositoryExecutionContext}

/**
  * Created by Lance on 2016-10-26.
  */
trait Service {}

trait UserService[User] extends Service {

  type Valid[A] = Future[A]
  type RepositoryAndContext = (UserRepository, UserRepositoryExecutionContext)
  type UserOperation[A] = Kleisli[Valid, RepositoryAndContext, A]

  def list: UserOperation[Seq[User]]
}