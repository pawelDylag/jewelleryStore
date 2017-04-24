package dao

import javax.inject.Inject

import models.{Category}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Category DAO
  * Created by paweldylag on 24/04/2017.
  */
class CategoryDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Categories = TableQuery[CategoryTable]

  def all(): Future[Seq[Category]] = db.run(Categories.result)

  def insert(category: Category): Future[Unit] = db.run(Categories += category).map { _ => () }

  def delete(id: Int): Future[Unit] = db.run(Categories.filter(_.id === id).delete).map(_ => ())

  def findById(id: Int): Future[Option[Category]] = db.run(Categories.filter(_.id === id).result.headOption)

  def update(id: Int, category: Category): Future[Unit] = {
    val categoryToUpdate: Category = category.copy(id)
    db.run(Categories.filter(_.id === category.id).update(categoryToUpdate)).map(_ => ())
  }

  private class CategoryTable(tag: Tag) extends Table[Category](tag, "CATEGORY") {

    def id = column[Int]("ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def description = column[String]("DESCRIPTION")

    def * = (name, description, id) <>(Category.tupled, Category.unapply _)
  }

}
