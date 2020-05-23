package controllers

import javax.inject.Inject
import models.Book
import play.api.mvc.InjectedController
import play.api.libs.json.Json._
import services.BookService

class BookStoreController @Inject()(bookService: BookService) extends InjectedController {

  def getBooks() = Action {

    val books = bookService.getBooks()
    Ok(views.html.books(stringify(toJson(books))))

  }

  def getBook(isbn: String) = Action {

    val book = bookService.getBook(isbn)
    Ok(views.html.books(stringify(toJson(book))))

  }

  def addBook = Action(parse.json) { implicit request =>

    val newBook = request.body.as[Book]
    val books = bookService.addBook(newBook)
    Ok(views.html.books(stringify(toJson(books))))

  }

}
