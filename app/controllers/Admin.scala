package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import dao.{CategoryDAO, ProductDAO}
import models.{Category, Product}
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.number
import play.api.mvc.{Action, Controller}

/**
  * Created by paweldylag on 11/04/2017.
  */
class Admin  @Inject()  (productDAO: ProductDAO, categoryDAO: CategoryDAO) extends Controller {

  val productForm = Form(
    mapping(
      "id" -> number,
      "categoryId" -> number,
      "name" -> text(),
      "description" -> text(),
      "price" -> number
    )(Product.apply)(Product.unapply)
  )

  val categoryForm = Form(
    mapping(
      "id" -> number,
      "name" -> text(),
      "description" -> text()
    )(Category.apply)(Category.unapply)
  )


  def index = Action.async {
    productDAO.all().map(products => Ok(views.html.index(products)))
  }

  def createproduct = Action {
    Ok(views.html.createproduct.render())
  }

  def newproduct = Action { implicit request =>
    val product: models.Product = productForm.bindFromRequest().get
    productDAO.insert(product)
    Ok(views.html.newproduct.render(product))
  }

  def newcategory = Action {implicit  request =>
    val category: models.Category = categoryForm.bindFromRequest().get
    categoryDAO.insert(category)
    Ok(views.html.newcategory.render(category))
  }

  def deleteproduct(id:Int) = Action { implicit request =>
    productDAO.delete(id)
    Redirect(routes.Application.index())
  }

  def applyproductchanges = Action { implicit request =>
    val updatedProduct: models.Product = productForm.bindFromRequest().get
    productDAO.update(updatedProduct.id,updatedProduct)
    Redirect(routes.Application.index())
  }

  def updateproduct(id:Int) = Action.async { implicit request =>
    val computerAndOptions = for {
      product <- productDAO.findById(id)
    } yield (product)

    computerAndOptions.map { case (computer) =>
      computer match {
        case Some(product) => Ok(views.html.updateproduct(product))
        case None => NotFound
      }
    }

  }

}
