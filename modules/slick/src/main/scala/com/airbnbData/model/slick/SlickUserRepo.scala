package com.airbnbData.model.slick

import java.util.UUID
import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import slick.jdbc.JdbcBackend.Database
import com.airbnbData.model._
import com.airbnbData.model.slick.dao.{Profile, UsersDAO}

import scala.concurrent.Future
import scala.language.implicitConversions



/**
 * A User Repository implemented with Slick, leveraging Slick code gen.
 *
 * Note that you must run "flyway/flywayMigrate" before "compile" here.
 */
@Singleton
class SlickUserRepo @Inject()(db: Database) extends UserRepo with Profile with UsersDAO {

  // Use the custom postgresql driver.
  override val profile: MyPostgresDriver = MyPostgresDriver

  import profile.api._

  private val queryById = Compiled(
    (id: Rep[UUID]) => Users.filter(_.id === id))

  def lookup(id: UUID)(implicit ec: UserRepoExecutionContext): Future[Option[User]] = {
    val f: Future[Option[UsersRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(usersRowToUser))
  }

  def all(implicit ec: UserRepoExecutionContext): Future[Seq[User]] = {
    val f = db.run(Users.result)
    f.map(seq => seq.map(usersRowToUser))
  }

  def update(user: User)(implicit ec: UserRepoExecutionContext): Future[Int] = {
    db.run(queryById(user.id).update(userToUsersRow(user)))
  }

  def delete(id: UUID)(implicit ec: UserRepoExecutionContext): Future[Int] = {
    db.run(queryById(id).delete)
  }

  def create(user: User)(implicit ec: UserRepoExecutionContext): Future[Int] = {
    db.run(
      Users += userToUsersRow(user.copy(createdAt = DateTime.now()))
    )
  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def userToUsersRow(user: User): UsersRow = {
    UsersRow(user.id, user.email, user.createdAt, user.updatedAt)
  }

  private def usersRowToUser(usersRow: UsersRow): User = {
    User(usersRow.id, usersRow.email, usersRow.createdAt, usersRow.updatedAt)
  }
}
