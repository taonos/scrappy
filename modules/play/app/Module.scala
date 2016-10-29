import javax.inject.{Inject, Provider, Singleton}

import com.google.inject.{AbstractModule, TypeLiteral}
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcBackend.Database
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment}
import com.airbnbData.service._
import com.airbnbData.model.User
import com.airbnbData.repository.{UserRepository, UserRepositoryExecutionContext}
import com.airbnbData.service.interpreter.UserServiceInterpreter
import com.airbnbData.slick.repository.interpreter.SlickUserRepositoryInterpreter

class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {

    bind(classOf[Config]).toInstance(configuration.underlying)
    bind(classOf[UserRepositoryExecutionContext]).toProvider(classOf[SlickUserRepositoryExecutionContextProvider])

    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[UserRepository]).to(classOf[SlickUserRepositoryInterpreter])

    bind(new TypeLiteral[UserService[User]] {}).to(classOf[UserServiceInterpreter])

    bind(classOf[UserRepositoryCloseHook]).asEagerSingleton()
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[Database] {

  private val db = Database.forConfig("myapp.database", config)

  override def get(): Database = db
}

@Singleton
class SlickUserRepositoryExecutionContextProvider @Inject()(actorSystem: akka.actor.ActorSystem) extends Provider[UserRepositoryExecutionContext] {
  private val instance = {
    val ec = actorSystem.dispatchers.lookup("myapp.database-dispatcher")
    new SlickUserRepositoryExecutionContext(ec)
  }

  override def get() = instance
}

class SlickUserRepositoryExecutionContext(ec: ExecutionContext) extends UserRepositoryExecutionContext {
  override def execute(runnable: Runnable): Unit = ec.execute(runnable)

  override def reportFailure(cause: Throwable): Unit = ec.reportFailure(cause)
}

/** Closes database connections safely.  Important on dev restart. */
class UserRepositoryCloseHook @Inject()(repo: UserRepository, lifecycle: ApplicationLifecycle) {
  private val logger = org.slf4j.LoggerFactory.getLogger("application")

  lifecycle.addStopHook { () =>
    Future.successful {
      logger.info("Now closing database connections!")
      repo.close()
    }
  }
}
