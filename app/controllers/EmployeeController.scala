package controllers

import dao.EmployeeDAO
import models.{APIErrorResponse, APIResponse, Employee}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmployeeController  @Inject()(employeeDAO: EmployeeDAO,val controllerComponents:ControllerComponents) extends BaseController
{
  def addEmployee():Action[AnyContent]=Action.async{ implicit request=>
    request.body.asJson match {
      case Some(json) =>
        val employee = json.asOpt[Employee].getOrElse(throw new NoSuchElementException("Please provide valid data to add an Employee"))
        employeeDAO.insertEmployee(employee).map { s => Ok(Json.toJson(s))} recover{
          case t:Throwable=>BadRequest(Json.toJson(APIErrorResponse("400",message = t.getLocalizedMessage)))
        }
      case None => Future.successful(BadRequest)
    }
  }

  def updateEmployee():Action[JsValue]=Action.async(parse.json){ implicit request=>
    request.body.validate[Employee] match {
      case s:JsSuccess[Employee]=>
        employeeDAO.updateEmployee(s.value)
        .map{
          case 0 => Ok(Json.toJson(APIErrorResponse("400",s"Could not find any employee with given id: ${s.value.employee_id} to update details")))
          case _=>Ok(Json.toJson(APIResponse(Seq(s.value),s"Successfully updated employee with id: ${s.value.employee_id}")))
        }
      case error:JsError=>Future.successful(BadRequest(Json.toJson(APIErrorResponse("error",s"Error due to $error"))))
    }
  }

  def getEmployeeByName(employee_name:String):Action[AnyContent]=Action.async{_=>
    employeeDAO.findByName(employee_name).map{s:Seq[Employee]=>
      checkIfDataIsEmpty(s)
      } recover{
      case t:Throwable=>BadRequest(Json.toJson(APIErrorResponse("400",t.getLocalizedMessage)))
    }
  }

  def getEmployeeById(employee_id:Int):Action[AnyContent]=Action.async{_=>
    employeeDAO.findById(employee_id).map{ s=>
      checkIfDataIsEmpty(s)
    } recover{
      case t:Throwable=>BadRequest(Json.toJson(APIErrorResponse("400",t.getLocalizedMessage)))
    }
  }

  def getAllEmployees:Action[AnyContent]=Action.async{ implicit request=>
    employeeDAO.listAllEmployees().map{s=>
      checkIfDataIsEmpty(s)
    } recover{
      case t:Throwable=>BadRequest(t.getLocalizedMessage)
    }
  }

  def deleteEmployeeByName(id:Int):Action[AnyContent]= Action.async { implicit request =>
    employeeDAO.deleteEmployeeById(id).map {
      case 0=> Ok(Json.toJson(APIErrorResponse("400",s"Could not find any employee to delete with id : $id")))
      case _=>Ok(Json.toJson(APIResponse(Seq.empty[Employee],s"Successfully deleted employee with id: $id")))
    } recover {
      case t: Throwable => BadRequest(Json.toJson(APIErrorResponse("400",t.getLocalizedMessage)))
    }
  }

  def checkIfDataIsEmpty(employees:Seq[Employee]):Result={
    employees.headOption match {
      case None=>Ok(Json.toJson(APIErrorResponse("error","Employees table is empty with no records")))
      case _=>Ok(Json.toJson(APIResponse(employees,"Employee records are retrieved successfully")))
    }
  }
}
