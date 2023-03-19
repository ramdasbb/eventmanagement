package controllers

import models.{Employee, ErrorMessage, SearchResponse, SuccessMessage}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class EmployeeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting  {

  "EmployeeController" should {

    "add an employee" in {
      val employee = Employee(1, "Employee", "9876543210", "employee@gmail.com", "near old school church", "Emp_1", "emp@123")
      val addRequest = FakeRequest(POST, "/api/addEmployee").withBody(Json.toJson(employee))
      val addResponse = route(app, addRequest).get
      status(addResponse) mustBe OK
      contentAsJson(addResponse).as[Employee].equals(employee)
    }

    "cannot add another employee with same id" in {
      val employee = Employee(1, "Emp", "9876543232", "emp@gmail.com", "near hospital", "Emp_2", "emp@123")
      val addRequest = FakeRequest(POST, "/api/addEmployee").withBody(Json.toJson(employee))
      val addResponse = route(app, addRequest).get
      status(addResponse) mustBe BAD_REQUEST
    }

    "cannot add another employee with same username" in {
      val employee = Employee(2, "Emp", "9876543222", "emp2@gmail.com", "near hospital", "Emp_1", "emp@123")
      val addRequest = FakeRequest(POST, "/api/addEmployee").withBody(Json.toJson(employee))
      val addResponse = route(app, addRequest).get
      status(addResponse) mustBe BAD_REQUEST
    }

    "edit the employee details" in {
      val employee_v1 = Employee(1,"Saga","9870654321","saga@gmail.com","opp. to main church","Emp_1","emp@123")
      val editRequest = FakeRequest(PUT, "/api/editEmployee").withBody(Json.toJson(employee_v1))
      val editResponse = route(app,editRequest).get
      status(editResponse) mustBe OK
      contentAsJson(editResponse).as[SuccessMessage].message mustBe s"successfully updated employee with id : ${employee_v1.employee_id}"
    }

    "search employee by id" in {
      val employee_v1 = Employee(1,"Saga","9870654321","saga@gmail.com","opp. to main church","Emp_1","emp@123")
      val searchRequest = FakeRequest(GET, "/api/search/byId/1")
      val searchResponse = route(app, searchRequest).get
      status(searchResponse) mustBe OK
      val content = contentAsJson(searchResponse).as[SearchResponse]
      content.message mustBe "Employee Data Retrieved Successfully."
      content.content.head mustBe employee_v1
    }

    "search employee by username" in {
      val employee_v1 = Employee(1,"Saga","9870654321","saga@gmail.com","opp. to main church","Emp_1","emp@123")
      val searchRequest = FakeRequest(GET, "/api/search/byUsername/Emp_1")
      val searchResponse = route(app, searchRequest).get
      status(searchResponse) mustBe OK
      val content = contentAsJson(searchResponse).as[SearchResponse]
      content.message mustBe "Employee Data Retrieved Successfully."
      content.content.head mustBe employee_v1
    }

    "delete the employee" in {
      val deleteRequest = FakeRequest(DELETE, "/api/employee/1")
      val deleteResponse = route(app,deleteRequest).get
      status(deleteResponse) mustBe OK
      contentAsJson(deleteResponse).as[SuccessMessage].message mustBe "Successfully deleted employee with id: 1"
    }

    "Cannot find the details to edit for the given employee id" in {
      val employee_v1 = Employee(1,"Saga","9870654321","saga@gmail.com","opp. to main church","Emp_1","emp@123")
      val editRequest = FakeRequest(PUT, "/api/editEmployee").withBody(Json.toJson(employee_v1))
      val editResponse = route(app,editRequest).get
      status(editResponse) mustBe OK
      contentAsJson(editResponse).as[ErrorMessage].message mustBe s"Couldn't find any employee with given id: ${employee_v1.employee_id} to edit his details."
    }

   "An Empty response is given for search when there are no employees with given employee id" in {
     val searchRequest = FakeRequest(GET, "/api/search/byId/1")
     val searchResponse = route(app, searchRequest).get
     status(searchResponse) mustBe OK
     val content = contentAsJson(searchResponse).as[SearchResponse]
     content.message mustBe "Sorry, There are no employees found to retrieve the data"
     content.content mustBe Seq.empty[Employee]
   }

   "An Empty Response is given for search when there are no employees with given employee username" in {
     val searchRequest = FakeRequest(GET, "/api/search/byUsername/Emp_1")
     val searchResponse = route(app, searchRequest).get
     status(searchResponse) mustBe OK
     val content = contentAsJson(searchResponse).as[SearchResponse]
     content.message mustBe "Sorry, There are no employees found to retrieve the data"
     content.content mustBe Seq.empty[Employee]
   }

  "Cannot delete an employee if the given employee id is not present in db." in {
    val deleteRequest = FakeRequest(DELETE, "/api/employee/1")
    val deleteResponse = route(app,deleteRequest).get
    status(deleteResponse) mustBe OK
    contentAsJson(deleteResponse).as[ErrorMessage].message mustBe "Couldn't find any employee to delete with id: 1"
  }
  }
}