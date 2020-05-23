package controllers

import javax.inject.Inject
import models.HelloWorld
import play.api.mvc.{BaseController, ControllerComponents, InjectedController}
import play.api.libs.json.Json._

class HelloWorldController  @Inject () extends InjectedController {

  def hello(module: String) = Action {
    Ok(toJson(HelloWorld(module)))
  }

  def helloView(module: String) = Action {
    Ok(views.html.hello(stringify(toJson(HelloWorld(module)))))
  }
}
