package controllers

import dao.EmployeeDao
import models.Employee

import javax.inject._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import services.EmployeeService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EmployeeController @Inject()(employeeDao: EmployeeDao, val employeeService: EmployeeService, val controllerComponents: ControllerComponents ) extends BaseController {


  def getEmployeeById(id: Long): Action[AnyContent] = Action.async {
    employeeDao.findById(id).map { employees =>
      if (employees.isEmpty) {
        NotFound("Employee with id " + id + " not found")
      } else {
        Ok(Json.toJson(employees.head))
      }
    }.recover {
      case ex =>
        InternalServerError("An error occurred while retrieving the employee with id " + id + ": " + ex.getMessage)
    }
  }

  def getEmployeeByName(name: String): Action[AnyContent] = Action.async {
    employeeDao.findByName(name).map { employees =>
      if (employees.isEmpty) {
        NotFound("Employee with name " + name + " not found")
      } else {
        Ok(Json.toJson(employees))
      }
    }.recover {
      case ex =>
        InternalServerError("An error occurred while retrieving the employees with name " + name + ": " + ex.getMessage)
    }
  }

  def getAllEmployees(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    employeeDao.getAll.map { employees =>
      Ok(Json.toJson(employees))
    }.recover {
      case ex =>
        InternalServerError("An error occurred while retrieving the employees: " + ex.getMessage)
    }
  }

  def addEmployee(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val emp = json.asOpt[Employee].getOrElse(throw new NoSuchElementException("Please Provide valid data"))
        employeeDao.add(emp).map { p =>
          Ok(Json.toJson(p))
        }
      case None =>
        Future.successful(BadRequest("Invalid Employee format"))
    }
  }

  def updateEmployeeById(id: Long): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Employee].map { updatedEmployee =>
      employeeDao.updateById(id, updatedEmployee).map {
        case 0 => NotFound("Employee with id " + id + " not found")
        case _ => Ok(Json.toJson(updatedEmployee))
      }
    }.getOrElse(Future.successful(BadRequest("Invalid employee format")))
  }

  def updateEmployee(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Employee] match {
      case e: JsSuccess[Employee] =>
        employeeDao.update(e.value).map{ _ =>
          println("successfully updated employee")
          Ok( Json.toJson(e.value))
        }
      case error: JsError => Future.successful( BadRequest("error message"+ error))
    }
  }


  def deleteEmployeeById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    employeeDao.delete(id).map { deletedCount =>
      deletedCount match {
        case 0 => NotFound("Employee with id " + id + " not found")
        case _ => Ok(s"Employee with id $id deleted successfully")
      }
    }.recover {
      case e: Exception => InternalServerError(e.getMessage)
    }
  }


  def deleteEmployeeByName(name: String): Action[AnyContent] = Action.async { implicit request =>
    employeeService.findAndDelete(name).map { deletedCount =>
      deletedCount match {
        case 0 => NotFound(s"No employee found with name $name")
        case _ => Ok(s"Employee with name $name deleted successfully")
      }
    }.recover {
      case e: Exception => InternalServerError(e.getMessage)
    }
  }
}
