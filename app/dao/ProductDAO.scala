package dao

/**
  * Created by kprzystalski on 16/04/16.
  */

import scala.concurrent.Future

import javax.inject.Inject
import models.Product
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile


class ProductDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Products = TableQuery[ProductsTable]

  def all(): Future[Seq[Product]] = db.run(Products.result)

  def insert(product: Product): Future[Unit] = db.run(Products += product).map { _ => () }

  def delete(id: Int): Future[Unit] = db.run(Products.filter(_.id === id).delete).map(_ => ())

  def findById(id: Int): Future[Option[Product]] = db.run(Products.filter(_.id === id).result.headOption)

  def update(id: Int, product: Product): Future[Unit] = {
    val productToUpdate: Product = product.copy(id)
    db.run(Products.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

  private class ProductsTable(tag: Tag) extends Table[Product](tag, "PRODUCT") {

    def id = column[Int]("ID", O.PrimaryKey)

    def categoryId = column[Int]("CATEGORY_ID")
//    def category = foreignKey("SUP_FK", categoryId, suppliers)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def name = column[String]("NAME")

    def description = column[String]("DESCRIPTION")

    def price = column[Int]("PRICE")

    def * = (name, description, price) <>(Product.tupled, Product.unapply _)
  }




}
