package controllers

import models._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Play.materializer
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class EmployeeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "EmployeeController" should {

    "add a new employee" in {
      val newEmployee = Employee(103, "akhil", "999999999999", "abc@gmail.com", "vijayawada", "u1", "p1")
      val json = Json.toJson(newEmployee)
      val fakeRequest = FakeRequest(POST, "/api/employee").withBody(json)
      val result = route(app,fakeRequest).get
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(newEmployee)
      contentAsJson(result).as[Employee].equals(newEmployee)
    }

    "cannot add another employee with same id" in {
      val employee = Employee(103, "ram", "987654321", "emp@gmail.com", "hyderabad", "Emp_2", "abc@123")
      val addRequest = FakeRequest(POST, "/api/employee").withBody(Json.toJson(employee))
      val addResponse = route(app, addRequest).get
      status(addResponse) mustBe BAD_REQUEST
    }

    "update an existing employee" in {
      val updatedEmployee = Employee(101, "ramasai", "999999999999", "abc@gmail.com", "vijayawada", "u1", "p1")
      val json = Json.toJson(updatedEmployee)
      val fakeRequest = FakeRequest(PUT, "/api/employee").withJsonBody(json)
      val result = route(app,fakeRequest).get
      status(result) mustBe OK
      contentAsJson(result).as[APIResponse].message mustBe s"Successfully updated employee with id: ${updatedEmployee.employee_id}"
    }

    "return an employee by name" in {
      val employee = Employee(102, "akhil", "999999999999", "mohanakhil.d@techsophy.com", "vijayawada", "mohanakhil", "test")
      val fakeRequest = FakeRequest(GET, s"/api/employee/byname/${employee.employee_name}")
      val result = route(app,fakeRequest).get
      status(result) mustBe OK
      val content = contentAsJson(result).as[APIResponse]
      content.message mustBe "Employee records are retrieved successfully"
      content.data.head mustBe employee
    }

    "return an employee by id" in {
      val employee = Employee(101, "ramasai", "999999999999", "abc@gmail.com", "vijayawada", "u1", "p1")
      val fakeRequest = FakeRequest(GET, s"/api/employee/byid/${employee.employee_id}")
      val result = route(app,fakeRequest).get
      status(result) mustBe OK
      val content = contentAsJson(result).as[APIResponse]
      content.message mustBe "Employee records are retrieved successfully"
      content.data.head mustBe employee
    }

    "return all employees " in {
      val employee1 = Employee(101, "ramasai", "999999999999", "abc@gmail.com", "vijayawada", "u1", "p1")
      val fakeRequest=FakeRequest(GET,"/api/employee/all")
      val result=route(app,fakeRequest).get
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      val content = contentAsJson(result).as[APIResponse]
      content.message mustBe "Employee records are retrieved successfully"
      content.data.head mustBe employee1
    }

    "delete employee by id" in {
      val employee = Employee(103, "akhil", "999999999999", "mohanakhil.d@techsophy.com", "vijayawada", "mohanakhil", "test")
      val fakeRequest=FakeRequest(DELETE,s"/api/employee/${employee.employee_id}")
      val result=route(app,fakeRequest).get
      status(result) mustBe OK
      contentAsJson(result).as[APIResponse].message mustBe "Successfully deleted employee with id: 103"
    }

    "Cannot find the details to update for the given id" in {
      val employee_v1 = Employee(1001, "mohan", "987654321", "abc@gmail.com", "hyd", "employee1", "abc@123")
      val fakeRequest = FakeRequest(PUT, "/api/employee").withBody(Json.toJson(employee_v1))
      val result = route(app, fakeRequest).get
      status(result) mustBe OK
      contentAsJson(result).as[APIErrorResponse].message mustBe s"Could not find any employee with given id: 1001 to update details"
    }

    "An Empty response is given for search when there are no employees with given employee id" in {
      val fakeRequest = FakeRequest(GET, "/api/employee/byid/1001")
      val result = route(app, fakeRequest).get
      status(result) mustBe OK
      val response = contentAsJson(result).as[APIErrorResponse]
      response.message mustBe "Employees table is empty with no records"
    }

    "An Empty Response is given for search when there are no employees with given employee username" in {
      val searchRequest = FakeRequest(GET, "/api/employee/byname/abc")
      val searchResponse = route(app, searchRequest).get
      status(searchResponse) mustBe OK
      val response = contentAsJson(searchResponse).as[APIErrorResponse]
      response.message mustBe  "Employees table is empty with no records"
    }

    "Cannot delete an employee if the given employee id is not present in db." in {
      val fakeRequest = FakeRequest(DELETE, "/api/employee/1001")
      val response = route(app, fakeRequest).get
      status(response) mustBe OK
      contentAsJson(response).as[APIErrorResponse].message mustBe "Could not find any employee to delete with id : 1001"
    }
  }
}

