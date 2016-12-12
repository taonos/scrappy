package com.airbnbData.service.interpreter

import com.airbnbData.model.query.User
import com.airbnbData.repository.{UserRepository, UserRepositoryExecutionContext}
import com.airbnbData.service.UserService

import scalaz._

/**
  * Created by Lance on 2016-10-29.
  */

class UserServiceInterpreter extends UserService[User] {
  override def list: UserOperation[Seq[User]] =
    Kleisli[Valid, (UserRepository, UserRepositoryExecutionContext),Seq[User]] {
      case (repo, ec) => repo.all(ec)
    }
}
