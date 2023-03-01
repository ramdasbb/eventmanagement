package controllers

import dao.StudentDao
import models.{Student, StudentUpdate}
import play.api.Logger
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json, OFormat}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.StudentService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class StudentController @Inject()(studentDao: StudentDao, studentService: StudentService, val controllerComponents: ControllerComponents ) extends BaseController {
  val logger: Logger = Logger.apply(this.getClass)

  case class StudentDelete(message:String)

  object StudentDelete {
    implicit val format: OFormat[StudentDelete] = Json.format[StudentDelete]
  }

  def getStudentByName(name: String): Action[AnyContent] = Action.async { _ =>
    studentDao.findByName(name).map{ students =>
      Ok( Json.toJson(students))
    }recover{
      case _: Throwable =>
        BadRequest(s"Not received student by name $name")
    }
  }

  def getStudent(id: Long): Action[AnyContent] = Action.async { _ =>
    studentDao.findById(id).map { students =>
      Ok(Json.toJson(students))
    } recover {
      case _: Throwable =>
        BadRequest(s"Not received student by id $id")
    }
  }

  def addStudent(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val student = json.asOpt[Student].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        studentDao.insert(student).map { p =>
          Ok(Json.toJson(p))
        }
      case None =>
        Future.successful(BadRequest)
    }
  }

  def updateStudent(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Student] match {
      case p: JsSuccess[Student] =>
        studentDao.update(p.value).map{ _ =>
         Ok(Json.toJson(StudentUpdate(p.value,s"successfully updated student with id : ${p.value.studentId}")))
        }
      case error: JsError => Future.successful( BadRequest("error message"+ error))
    }
  }

  def deleteStudent(name: String): Action[AnyContent] = Action.async { implicit request =>
    studentService.findAndDelete(name).map{
      _ =>  Ok(Json.toJson(StudentDelete(s"Successfully deleted student $name")))
    } recover {
      case t: Throwable =>
        Ok(Json.toJson(StudentDelete(t.getLocalizedMessage)))
    }
  }

  def all: Action[AnyContent] = Action.async { implicit request =>
    studentDao.list().map { students =>
      Ok(Json.toJson(students))
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

}
