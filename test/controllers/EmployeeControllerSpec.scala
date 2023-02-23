package controllers

import controllers.EmployeeController
import services.EmployeeService
import dao.EmployeeDao
import models.Employee
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Play.materializer
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar {

  val mockEmployeeDao: EmployeeDao = mock[EmployeeDao]
  val mockEmployeeService: EmployeeService = mock[EmployeeService]

  val employeeController = new EmployeeController(mockEmployeeDao, mockEmployeeService, stubControllerComponents())

  "EmployeeController" should {

    "return a JSON object with an employee when given a valid ID" in {
      val employee = Employee(10L, "John Dummy", "1234567890", "john.doe@example.com", "123 Main St", "jdoe", "password")
      when(mockEmployeeDao.findById(10L)).thenReturn(Future.successful(Seq(employee)))

      val result = employeeController.getEmployeeById(10L).apply(FakeRequest(GET, "/employee/byId/1"))

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(employee)
    }

    "return a 404 Not Found error when given an invalid ID" in {
      when(mockEmployeeDao.findById(any())).thenReturn(Future.successful(Seq()))

      val result = employeeController.getEmployeeById(100L).apply(FakeRequest(GET, "/employee/byId/100"))

      status(result) mustBe NOT_FOUND
      contentAsString(result) mustBe "Employee with id 100 not found"
    }

    "return a JSON array with employees when given a valid name" in {
      val employees = Seq(Employee(1L, "John Doe", "1234567890", "john.doe@example.com", "123 Main St", "jdoe", "password"), Employee(2L, "Jane Doe", "0987654321", "jane.doe@example.com", "456 High St", "jadoe", "password"))
      when(mockEmployeeDao.findByName("John Doe")).thenReturn(Future.successful(employees))

      val result = employeeController.getEmployeeByName("John Doe").apply(FakeRequest(GET, "/employee/byName/John Doe"))

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(employees)
    }

    "return a 404 Not Found error when given an invalid name" in {
      when(mockEmployeeDao.findByName(any())).thenReturn(Future.successful(Seq()))

      val result = employeeController.getEmployeeByName("Doe").apply(FakeRequest(GET, "/employee/byName/?name=Doe"))

      status(result) mustBe NOT_FOUND
      contentAsString(result) mustBe "Employee with name Doe not found"
    }


    "return a JSON array with all employees" in {
      val employees = Seq(Employee(1L, "John Doe", "1234567890", "john.doe@example.com", "123 Main St", "jdoe", "password"), Employee(2L, "Jane Doe", "0987654321", "jane.doe@example.com", "456 High St", "jadoe", "password"))
      when(mockEmployeeDao.getAll).thenReturn(Future.successful(employees))

      val result = employeeController.getAllEmployees().apply(FakeRequest(GET, "/employee/all"))

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe Json.toJson(employees)
    }

    "add a new employee" in {
      val newEmployee = Employee(3L, "Bob Smith", "1112223333", "bob.smith@example.com", "789 Elm St", "bsmith", "password")
      when(mockEmployeeDao.add(newEmployee)).thenReturn(Future.successful(newEmployee))

      val jsonBody = Json.toJson(newEmployee)
      val result = employeeController.addEmployee().apply(FakeRequest(POST, "/employee/add").withJsonBody(jsonBody))

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(newEmployee)
    }

    "return 200 and updated employee when valid ID and employee are given" in {
      val updatedEmployee = Employee(2L, "Jane Doe", "5555555555", "jane.doe@example.com", "456 High St", "jadoe", "newpassword")
      when(mockEmployeeDao.updateById(2L, updatedEmployee)).thenReturn(Future.successful(1))

      val jsonBody = Json.toJson(updatedEmployee)
      val result = employeeController.updateEmployeeById(2L).apply(FakeRequest(PUT, "/employee/update/2").withJsonBody(jsonBody))

      status(result) mustBe OK
      contentAsJson(result) mustBe jsonBody
    }

    "return 404 when invalid ID is given" in {
      val updatedEmployee = Employee(2L, "Jane Doe", "5555555555", "jane.doe@example.com", "456 High St", "jadoe", "newpassword")
      when(mockEmployeeDao.updateById(100L, updatedEmployee)).thenReturn(Future.successful(0))

      val jsonBody = Json.toJson(updatedEmployee)
      val result = employeeController.updateEmployeeById(100L).apply(FakeRequest(PUT, "/employee/update/100").withJsonBody(jsonBody))

      status(result) mustBe NOT_FOUND
      contentAsString(result) mustBe "Employee with id 100 not found"
    }

    "return 400 when invalid employee data is given" in {
      val invalidEmployeeJson = Json.obj(
        "name" -> "Jane Doe",
        "phone" -> "5555555555",
        "email" -> "jane.doe@example.com",
        "address" -> "456 High St",
        "username" -> "jadoe",
        "password" -> "newpassword",
        "invalidField" -> "some value"
      )

      val result = employeeController.updateEmployeeById(2L).apply(FakeRequest(PUT, "/employee/update/2").withJsonBody(invalidEmployeeJson))

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe "Invalid employee format"
    }


    "delete an existing employee by id" in {
      val idToDelete = 1L
      when(mockEmployeeDao.delete(idToDelete)).thenReturn(Future.successful(1))
      val result = employeeController.deleteEmployeeById(idToDelete).apply(FakeRequest(DELETE, s"/employee/delete/$idToDelete"))
      status(result) mustBe OK
      contentAsString(result) mustBe s"Employee with id $idToDelete deleted successfully"
    }

    "return 404 Not Found when attempting to delete non-existent employee by id" in {
      val idToDelete = 1L
      when(mockEmployeeDao.delete(idToDelete)).thenReturn(Future.successful(0))
      val result = employeeController.deleteEmployeeById(idToDelete).apply(FakeRequest(DELETE, s"/employee/delete/$idToDelete"))
      status(result) mustBe NOT_FOUND
      contentAsString(result) mustBe s"Employee with id $idToDelete not found"
    }

    "return 500 Internal Server Error when an error occurs during delete by id" in {
      val idToDelete = 1L
      when(mockEmployeeDao.delete(idToDelete)).thenReturn(Future.failed(new RuntimeException("An error occurred during delete")))
      val result = employeeController.deleteEmployeeById(idToDelete).apply(FakeRequest(DELETE, s"/employee/delete/$idToDelete"))
      status(result) mustBe INTERNAL_SERVER_ERROR
      contentAsString(result) mustBe "An error occurred during delete"
    }


  }
}