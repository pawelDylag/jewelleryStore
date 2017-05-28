package dao

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.{User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by paweldylag on 25/04/2017.
  */
class UserDAO  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]  {

  import driver.api._

  private val users = TableQuery[UserTable]

  def all(): Future[Seq[User]] = db.run(users.result)

  def insert(user: User): Future[Unit] = db.run(users += user).map { _ => () }

  def delete(email: String): Future[Unit] = db.run(users.filter(_.email === email).delete).map(_ => ())

  def findById(email: String): Future[Option[User]] = db.run(users.filter(_.email === email).result.headOption)

  def update(email: String, user: User): Future[Unit] = {
    val userToUpdate: User = user.copy(email)
    db.run(users.filter(_.email === email).update(userToUpdate)).map(_ => ())
  }

  private class UserTable(tag: Tag) extends Table[User](tag, "USER") {

    def email = column[String]("EMAIL")

    def password = column[String]("PASSWORD")

    def * = (email, password) <>(User.tupled, User.unapply _)
  }

}
