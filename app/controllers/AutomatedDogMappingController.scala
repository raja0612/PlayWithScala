package controllers

import javax.inject._
import models.Dog
import play.api.libs.json.Json.stringify
import play.api.libs.json.{JsError, JsPath, JsResult, JsSuccess, JsValue, Json}
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class AutomatedDogMappingController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {


  def readDog  =  Action(parse.json)   { implicit  request =>
    println("In readDog method")
    val dogJson: JsValue = Json.parse(stringify(request.body)) // convert string to JsValue (Json)

    val  dogFromJson:JsResult[Dog] = Json.fromJson[Dog](dogJson) // read dog json format to Dog class, implicit conversion

    dogFromJson  match {
      case JsSuccess(dog: Dog, path: JsPath) => Ok(Json.toJson(s"Hi from ${dog.name}"))
      case e @ JsError(_) => Ok(Json.toJson("Oh no, I am mad at you!"))
    }

  }
}
