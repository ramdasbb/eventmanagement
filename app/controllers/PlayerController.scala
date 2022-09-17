package controllers

import models.Player
import javax.inject._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class PlayerController @Inject()(val controllerComponents: ControllerComponents ) extends BaseController {
  val logger = Logger.apply(this.getClass)

  // Autowired in spring

  def getPlayer: Action[AnyContent] = Action { request =>
    // This data is coming from DB
    val player = Player("Dasuan", "Sri", Seq("Captain","Batsman"))
    Ok( Json.toJson(player))
  }

  /* DB integraion
      CRUD
  *   frontend expects -
  *   GET get data
  *   PUT update data
  *   POST add data
  *   search data
      Delete operation.
  * */

  def addPlayer: Action[AnyContent] = Action { request =>
    // This data is coming from UI
    // restrict only
    request.body.asJson match {
      case Some(json) =>
          val player = json.asOpt[Player].getOrElse(throw new NoSuchElementException("Please provide valid data"))
          println("Added player successfully")
          println("Added player successfully")
          // CALL DATABASE connection and db object here and store.
        Ok(Json.toJson(player))
      case None =>
        BadRequest
    }
  }

}