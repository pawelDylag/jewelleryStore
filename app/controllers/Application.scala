package controllers

import dao.ProductDAO
import models.Product
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.number
import play.api.mvc._
import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application @Inject() (productDAO: ProductDAO) extends Controller {

  val productForm = Form(
    mapping(
      "name" -> text(),
      "description" -> text(),
      "price" -> number
    )(Product.apply)(Product.unapply)
  )

  def index = Action.async {
    productDAO.all().map( products => Ok(views.html.index(products)))
  }

  def createproduct = Action {
    Ok(views.html.createproduct.render())
  }

  def newproduct = Action { implicit request =>
      val product: models.Product = productForm.bindFromRequest().get
      productDAO.insert(product)
      Ok(views.html.newproduct.render(product))
  }

  def deleteproduct(name:String) = Action { implicit request =>
    productDAO.delete(name)
    Redirect(routes.Application.index())
  }

  def applyproductchanges = Action { implicit request =>
    val updatedProduct: models.Product = productForm.bindFromRequest().get
    productDAO.update(updatedProduct.name,updatedProduct)
    Redirect(routes.Application.index())
  }

  def updateproduct(name:String) = Action.async { implicit request =>
    val computerAndOptions = for {
      product <- productDAO.findById(name)
    } yield (product)

    computerAndOptions.map { case (computer) =>
      computer match {
        case Some(product) => Ok(views.html.updateproduct(product))
        case None => NotFound
      }
    }

  }
}