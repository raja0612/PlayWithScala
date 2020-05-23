package models

case class Dog(name: String, breed: String, color: Option[String]);

object Dog {

  import play.api.libs.json._


  implicit val dogReads = Json.reads[Dog]
  implicit val dogWrites = Json.writes[Dog]
  implicit val dogFormat = Json.format[Dog]

}
