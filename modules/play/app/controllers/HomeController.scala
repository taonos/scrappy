package controllers

import javax.inject.{Inject, Singleton}

import com.airbnbData.model.query.User
import play.api.mvc._
import com.airbnbData.repository.{UserRepository, UserRepositoryExecutionContext}
import com.airbnbData.service.UserService

@Singleton
class HomeController @Inject() (userService: UserService[User], userRepository: UserRepository, userRepositoryExecutionContext: UserRepositoryExecutionContext) extends Controller {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  implicit val ec = userRepositoryExecutionContext

  def index = Action.async {
    logger.info("Calling index")
    userService
      .list
      .run((userRepository, ec))
      .map { users =>
        logger.info(s"Calling index: users = ${users}")
        Ok(views.html.index(users))
      }
  }

}
