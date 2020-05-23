package controllers

import javax.inject.Inject
import models._
import play.api.mvc.{Action, InjectedController}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._

class PlaceJsonController @Inject()() extends InjectedController {
  /*
    Open Postman  POST : localhost:9000/place
    body :
    {
      "name" : "Watership Down",
      "location" : {
        "lat" : 51.235685,
        "long" : -1.309197
      },
      "residents" : [ {
        "name" : "Fiver",
        "age" : 4,
        "role" : null
      }, {
        "name" : "Bigwig",
        "age" : 6,
        "role" : "Owsla"
      } ]
    }

   */
  def readPlace = Action(parse.json) { implicit request =>
    val json: JsValue = Json.parse(stringify(request.body))

    println("Json JsValue", json)

    //simple path
    val latPath = JsPath \ "location" \ "lat"
    val longPath = JsPath \ "location" \ "long"

    //Recursive Path
    val namesPath = JsPath \\ "name"

    //Indexed Path
    val firstResidentPath = (JsPath \ "residents") (0)

    val nameReads: Reads[String] = (JsPath \ "name").read[String]

    implicit val locationReads: Reads[Location] = (
      (JsPath \ "lat").read[Double] and
        (JsPath \ "long").read[Double]
      ) (Location.apply _)


    val nameResult: JsResult[String] = json.validate[String](nameReads)

    nameResult match {
      case s: JsSuccess[String] => println("Name:   " + s.get)
      case e: JsError => println("Errors:   " + JsError.toJson(e).toString())
    }

    val improvedNameReads = (JsPath \ "name").read[String](minLength[String](2))

    println("improvedNameReads", improvedNameReads)


    moreRead(json)

    Ok(json)

  }

  def moreRead(json: JsValue): Unit = {
    println("MORE ABOUT READ", json)

    implicit val locationReads: Reads[Location] = (
      (JsPath \ "lat").read[Double](min(-90.0).keepAnd(max(90.0))) and
        (JsPath \ "long").read[Double](min(-180.0).keepAnd(max(180.0)))
      ) (Location.apply _)

    implicit val residentReads: Reads[Resident] = (
      (JsPath \ "name").read[String](minLength[String](2)) and
        (JsPath \ "age").read[Int](min(0).keepAnd(max(150))) and
        (JsPath \ "role").readNullable[String]
      ) (Resident.apply _)

    implicit val placeReads: Reads[Place] = (
      (JsPath \ "name").read[String](minLength[String](2)) and
        (JsPath \ "location").read[Location] and
        (JsPath \ "residents").read[Seq[Resident]]
      ) (Place.apply _)

    json.validate[Place] match {
      case JsSuccess(place, _) => {
        val _: Place = place
        println("do something with Place", place)
      }
      case e: JsError => {
        println("error handling")
      }
    }

  }

  def writePlace() = Action {

    implicit val locationWrites: Writes[Location] = (
      (JsPath \ "lat").write[Double] and
        (JsPath \ "long").write[Double]
      ) (unlift(Location.unapply))

    implicit val residentWrites: Writes[Resident] = (
      (JsPath \ "name").write[String] and
        (JsPath \ "age").write[Int] and
        (JsPath \ "role").writeNullable[String]
      ) (unlift(Resident.unapply))

    implicit val placeWrites: Writes[Place] = (
      (JsPath \ "name").write[String] and
        (JsPath \ "location").write[Location] and
        (JsPath \ "residents").write[Seq[Resident]]
      ) (unlift(Place.unapply))

    val place = Place(
      "Herndon US",
      Location(51.36, 45.46),
      Seq(
        Resident("Raj", 29, Some("Software Engineer")),
        Resident("Max", 4, None)
      )
    )
    Ok(Json.toJson(place))
  }


  /*
   {
     "name": "Raj",
     "friends": [
      {"name": "Srujan"},
      {"name": "Mahesh"},
      {"name": "Max"}
     ]
 }
    */
  case class User(name: String, friends: Seq[User])

  def recursiveReads = Action(parse.json) { implicit request =>
    val json: JsValue = Json.parse(stringify(request.body))
    implicit lazy val userReads: Reads[User] = (
      (__ \ "name").read[String] and
        (__ \ "friends").lazyRead(Reads.seq[User](userReads))
      ) (User)

    implicit lazy val userWrites: Writes[User] = (
      (__ \ "name").write[String] and
        (__ \ "friends").lazyWrite(Writes.seq[User](userWrites))
      ) (unlift(User.unapply))

    Ok(Json.toJson(json))

  }

  /*
    {
      "name": "Raj",
      "age": 29
    }
   */

  case class Person(name: String, age: Int)

  def readAndWritePerson = Action(parse.json) { implicit request =>

    val readStudent: Reads[Person] = (
      (JsPath \ "name").read[String](minLength[String](2)) and
        (JsPath \ "age").read[Int](min(0).keepAnd(max(150)))
      ) (Person.apply _)

    val writeStudent: Writes[Person] = (
      (JsPath \ "name").write[String] and
        (JsPath \ "age").write[Int]
      ) (unlift(Person.unapply))

    implicit val studentFormat: Format[Person] = Format(readStudent, writeStudent)
    val json: JsValue = Json.parse(stringify(request.body))

    json.validate[Person] match {
      case JsSuccess(person, _) if person.age > 18 => Ok(Json.toJson(s"congrats ${person.name} you are eligible to VOTE"))
      case JsSuccess(person, _) if person.age < 18 => Ok(Json.toJson(s"Sorry ${person.name} you are not eligible to VOTE"))
      case e: JsError => Ok(Json.toJson("Sorry Something went wrong"))
    }
  }


}
