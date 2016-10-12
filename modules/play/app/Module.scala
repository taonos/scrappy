import javax.inject.{Inject, Provider, Singleton}

import com.airbnbData.model.slick.SlickUserRepo
import com.airbnbData.model.{UserRepo, UserRepoExecutionContext}
import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment}

import scala.concurrent.{ExecutionContext, Future}

class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {

    bind(classOf[Config]).toInstance(configuration.underlying)
    bind(classOf[UserRepoExecutionContext]).toProvider(classOf[SlickUserDAOExecutionContextProvider])

    bind(classOf[slick.jdbc.JdbcBackend.Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[UserRepo]).to(classOf[SlickUserRepo])

    bind(classOf[UserDAOCloseHook]).asEagerSingleton()
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[slick.jdbc.JdbcBackend.Database] {

  private val db = slick.jdbc.JdbcBackend.Database.forConfig("myapp.database", config)

  override def get(): slick.jdbc.JdbcBackend.Database = db
}

@Singleton
class SlickUserDAOExecutionContextProvider @Inject() (actorSystem: akka.actor.ActorSystem) extends Provider[UserRepoExecutionContext] {
  private val instance = {
    val ec = actorSystem.dispatchers.lookup("myapp.database-dispatcher")
    new SlickUserDAOExecutionContext(ec)
  }

  override def get() = instance
}

class SlickUserDAOExecutionContext(ec: ExecutionContext) extends UserRepoExecutionContext {
  override def execute(runnable: Runnable): Unit = ec.execute(runnable)

  override def reportFailure(cause: Throwable): Unit = ec.reportFailure(cause)
}

/** Closes database connections safely.  Important on dev restart. */
class UserDAOCloseHook @Inject()(dao: UserRepo, lifecycle: ApplicationLifecycle) {
  private val logger = org.slf4j.LoggerFactory.getLogger("application")

  lifecycle.addStopHook { () =>
    Future.successful {
      logger.info("Now closing database connections!")
      dao.close()
    }
  }
}
