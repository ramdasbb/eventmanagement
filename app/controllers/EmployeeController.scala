package controllers

import dao.EmployeeDao
import models.{Employee, ErrorMessage, SearchResponse, SuccessMessage}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EmployeeController @Inject()(employeeDao: EmployeeDao,val controllerComponents: ControllerComponents ) extends BaseController {

  val logger: Logger = Logger.apply(this.getClass)

  def addEmployee(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val employee = json.asOpt[Employee].getOrElse(throw new NoSuchElementException("Please provide valid data to add an employee"))
        employeeDao.save(employee).map { p => Ok(Json.toJson(p)) } recover {
          case t: Throwable => BadRequest(Json.toJson(ErrorMessage(message = t.getLocalizedMessage)))
        }
      case None => Future.successful(BadRequest)
    }
  }

  def editEmployee(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Employee] match {
      case p: JsSuccess[Employee] =>
        employeeDao.update(p.value).map {
          {
            case 0 => Ok(Json.toJson(ErrorMessage(message = s"Couldn't find any employee with given id: ${p.value.employee_id} to edit his details.")))
            case _ => Ok(Json.toJson(SuccessMessage(s"successfully updated employee with id : ${p.value.employee_id}")))
          }
        }
      case error: JsError => Future.successful(BadRequest(Json.toJson(ErrorMessage(message = "Error Due to: " + error))))
    }
  }

  def deleteEmployee(id: Int): Action[AnyContent] = Action.async { implicit request =>
    employeeDao.delete(id).map {
      case 0 => Ok(Json.toJson(ErrorMessage(message = s"Couldn't find any employee to delete with id: $id")))
      case _ => Ok(Json.toJson(SuccessMessage(s"Successfully deleted employee with id: $id")))
    } recover {
      case t: Throwable => BadRequest(Json.toJson(ErrorMessage(message = t.getLocalizedMessage)))
    }
  }

  def searchById(id: Int): Action[AnyContent] = Action.async { implicit request =>
    employeeDao.findById(id).map { employees: Seq[Employee] =>
      checkEmptyResponse(employees)
      }recover {
      case t: Throwable => BadRequest(Json.toJson(ErrorMessage(message = t.getLocalizedMessage)))
    }
  }

  def searchByUserName(userName: String): Action[AnyContent] = Action.async { implicit request =>
    employeeDao.findByName(userName).map { employees: Seq[Employee] =>
      checkEmptyResponse(employees)
    } recover {
    case t: Throwable => BadRequest(Json.toJson(ErrorMessage(message = t.getLocalizedMessage)))
  }
}

  def checkEmptyResponse(employeeData:Seq[Employee]) : Result = {
      employeeData.headOption match {
      case None => Ok(Json.toJson(SearchResponse("Sorry, There are no employees found to retrieve the data",Seq.empty[Employee])))
      case _ => Ok(Json.toJson(SearchResponse("Employee Data Retrieved Successfully.",employeeData)))
    }
  }
}
