package controllers

import dao.StudentDAO
import models.Student
import play.api.Logger
import play.api.libs.json.{JsSuccess, JsValue, Json, OFormat}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.StudentService

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

/**
the @Singleton annotation is used to ensure that there is only one instance of StudentController in the application,
 This line defines the StudentController class, which extends the BaseController class. The @Inject() annotation indicates that the constructor for StudentController should use dependency injection to obtain the studentDAO, studentService, and controllerComponents objects.

The studentDAO and studentService parameters are instances of the StudentDAO and StudentService classes, respectively. These objects are used to interact with the database and perform business logic related to the Student entity.
 The controllerComponents parameter is an instance of the ControllerComponents class, which provides access to various components that are needed to create and handle HTTP requests in a Play Framework application.
 */

@Singleton
class StudentController @Inject()(studentDAO:StudentDAO,studentService:StudentService, val controllerComponents: ControllerComponents) extends BaseController
{
  val logger:Logger=Logger.apply(this.getClass)

  /**
   *  this method handles a POST request to add a new Student object to the database. It first extracts the JSON data from the request body, converts it to a Student object, and then inserts it into the database using the studentDAO object. Finally, it returns an Ok response containing the newly created Student object as JSON data, or a BadRequest response if the request body does not contain valid JSON data.

   */

  case class DuplicateException(status:String,message:String)

  implicit val format: OFormat[DuplicateException] = Json.format[DuplicateException]

  def addStudent():Action[AnyContent]=Action.async{ implicit request=>
    request.body.asJson match {
      case Some(json)=>
        val student=json.asOpt[Student].getOrElse(throw new NoSuchElementException("Please provide valid data"))
          studentDAO.insertStudent(student)
            .map {
              s => Ok(Json.toJson(s))
            }recover{
          case e1:Exception=>BadRequest(Json.toJson(DuplicateException("error","duplicate record can not be allowed")))
        }
      case None=>Future.successful(BadRequest)
        }
    }

  def updateStudent():Action[JsValue]=Action.async(parse.json){ implicit request=>
    request.body.validate[Student] match {
      case s:JsSuccess[Student]=>studentDAO.update(s.value).map{_=>
        Ok("successfully updated student"+Json.toJson(s.value))
      }
    }
  }

  def getStudentByName(name:String):Action[AnyContent]=Action.async{_=>
    studentDAO.findByName(name).map{s=>
    Ok(Json.toJson(s))
  }recover{
      case _:Throwable=>BadRequest(s"No student in database by Name $name")
    }
  }

  def getStudentById(id:Long):Action[AnyContent]=Action.async{_=>
    studentDAO.findById(id).map{
      s=>Ok(Json.toJson(s))
    }recover{
      case _:Throwable=>BadRequest(s"No student in database with id $id")
    }
  }

  def getAllStudents:Action[AnyContent]=Action.async{ implicit request=>
    studentDAO.listAllStudents().map{s=>Ok(Json.toJson(s))}recover{
      case t:Throwable=>BadRequest(t.getLocalizedMessage)
    }
  }

  case class StudentDelete(message:String)

  object StudentDelete {
    implicit val format: OFormat[StudentDelete] = Json.format[StudentDelete]
  }

  def deleteStudentByName(name: String): Action[AnyContent] = Action.async { implicit request =>
    studentService.findAndDeleteStudent(name).map {
      _ => Ok(Json.toJson(StudentDelete(s"Successfully deleted student $name")))
    } recover {
      case t: Throwable =>
        Ok(Json.toJson(StudentDelete(t.getLocalizedMessage)))
    }
  }
}
