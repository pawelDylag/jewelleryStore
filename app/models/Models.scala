package models

case class Product(id: Int, categoryId: Int, name: String, description: String, price: Int)

case class Category(id: Int, name: String, description: String)

case class User (email: String, password: String);
