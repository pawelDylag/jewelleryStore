package controllers

import dao.{CategoryDAO, ProductDAO}
import models.Product
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.number
import play.api.mvc._
import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application @Inject() (productDAO: ProductDAO, categoryDAO: CategoryDAO) extends Controller {

  def index = Action.async {
    productDAO.all().map( products => Ok(views.html.index(products)))
  }

  def categories = Action.async {
    categoryDAO.all().map( categories => Ok(views.html.categories(categories)))
  }

}