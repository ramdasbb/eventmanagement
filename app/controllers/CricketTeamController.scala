package controllers

import dao.CricketTeamDao
import models.CricketTeam
import play.api.libs.json.Json

import javax.inject._
import play.api.mvc._
import services.CricketTeamService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CricketTeamController @Inject()(val teamDao: CricketTeamDao, val teamService: CricketTeamService, val controllerComponents: ControllerComponents ) extends BaseController {

  def addTeam(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val team = json.asOpt[CricketTeam].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        println("Team is added successfully")
        teamDao.create(team).map { data => Ok(Json.toJson(data)) }
      case None => Future.successful(BadRequest)
    }
  }

  def all(): Action[AnyContent] = Action.async { implicit request =>
    teamDao.list().map { players =>
      Ok(Json.toJson(players))
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

  def getTeamWithMaxScore(name: String): Action[AnyContent] = Action.async { implicit request =>
    teamService.getTeamWithMaxScoreByTournament(name).map {
      case (team, score) =>
        Ok(Json.obj(
          "team" -> Json.toJson(team),
          "score" -> score
        ))
    } recover {
      case t: Throwable => BadRequest(t.getLocalizedMessage)
    }
  }


  def deleteTeam(name: String): Action[AnyContent] = Action.async { implicit request =>
    teamService.findAndDelete(name).map {
      _ => Ok(s"Successfully deleted team ${name}")
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }
}
