package controllers

import dao.CricketPlayerDao
import models.CricketPlayer

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.CricketPlayerService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CricketPlayerController @Inject()(playerDao: CricketPlayerDao, val playerService: CricketPlayerService, val controllerComponents: ControllerComponents ) extends BaseController {

  def addPlayer(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val player = json.asOpt[CricketPlayer].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        playerDao.add(player).map { data => Ok(Json.toJson(data)) }
      case None => Future.successful(BadRequest)
    }
  }

  def all(): Action[AnyContent] = Action.async { implicit request =>
    playerDao.list().map { players =>
      Ok(Json.toJson(players))
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }


  def getMaxScorerInTournament(name: String): Action[AnyContent] = Action.async { implicit request =>
    playerService.getMaxScorerInTournament(name).map {
      case (player, score) =>
        Ok(Json.obj(
          "player" -> Json.toJson(player),
          "score" -> score
        ))
    } recover {
      case t: Throwable => BadRequest(t.getLocalizedMessage)
    }
  }


  def deletePlayer(name: String): Action[AnyContent] = Action.async { implicit request =>
    playerService.findAndDelete(name).map {
      _ => Ok(s"Successfully deleted player ${name}")
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }
}
