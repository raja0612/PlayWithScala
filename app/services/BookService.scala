package services

import models.Book

class BookService {

  def getBooks(): List[Book] = books

  def getBook(isbn: String): Option[Book] = {

   val mybook = books.filter { book =>
     book.isbn == isbn
   }
    mybook.headOption
  }

  def addBook(book: Book) = {
    books :+= book
    books
  }

  var books:List[Book] = List[Book](
    Book("ISBN01","Scala Reactive Programming", "Rambabu Posa", 550, 35.50),
    Book("ISBN02","Scala 2.13 In Depth", "Lokesh Posa", 420, 25.50),
    Book("ISBN03","Play JSON In Practice", "Rams", 510, 31.50),
    Book("ISBN05","Scala In Depth", "Rambabu Posa", 750, 38.90)
  )

}

