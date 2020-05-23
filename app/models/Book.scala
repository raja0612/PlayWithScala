package models
import play.api.libs.json.Json

case class Book(isbn: String, title: String, author: String, noofpages: Int, price: Double)


object Book {
  implicit val format = Json.format[Book]

  /*
  Manual reads and writes
   implicit val reads: Reads[Book] = (
      (JsPath \ "isbn").read[String] and
      (JsPath \ "title").read[String] and
      (JsPath \ "author").read[String] and
      (JsPath \ "noofpages").read[Int] and
      (JsPath \ "price").read[Double]
    )(Book.apply _)

  implicit val writes: Writes[Book] = (
      (JsPath \ "isbn").write[String] and
      (JsPath \ "title").write[String] and
      (JsPath \ "author").write[String] and
      (JsPath \ "noofpages").write[Int] and
      (JsPath \ "price").write[Double]
    )(unlift(Book.unapply))



   */
}
