package controllers

import dao.{AudienceDao, CricketPlayerDao}
import models.Audience

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AudienceController @Inject()(audienceDao: AudienceDao, cricketPlayerDao: CricketPlayerDao, val controllerComponents: ControllerComponents ) extends BaseController {

  def addAudience(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val audience = json.asOpt[Audience].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        audienceDao.add(audience).map { data => Ok(Json.toJson(data)) }
      case None => Future.successful(BadRequest)
    }
  }

  def all(): Action[AnyContent] = Action.async { implicit request =>
    audienceDao.list().map { audience =>
      Ok(Json.toJson(audience))
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

  def deleteAudience(id: Long): Action[AnyContent] = Action.async { implicit request =>
    audienceDao.delete(id).map {
      _ => Ok(s"Successfully deleted audience ${id}")
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

  def updateGuessedScore(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    val id = (json \ "id").as[Long]
    val guessedScore = (json \ "guessed_score").as[Int]

    cricketPlayerDao.findById(id).map {
      case Some(cricketPlayer) =>
        val updatedCricketPlayer = cricketPlayer.copy(score = guessedScore)
        cricketPlayerDao.update(updatedCricketPlayer)
        Ok(Json.obj("message" -> s"Updated score for player ${updatedCricketPlayer.name}"))
      case None =>
        NotFound(Json.obj("message" -> s"Player with id $id not found"))
    }
  }
}
