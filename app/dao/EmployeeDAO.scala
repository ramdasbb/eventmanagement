package dao

import models.Employee
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeDAO @Inject() (dbConfigProvider:DatabaseConfigProvider)(implicit ec:ExecutionContext)
{
  val dbConfig:DatabaseConfig[JdbcProfile]=dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class EmployeeTable(tag:Tag) extends Table[Employee](tag,"employee")
  {
    def employeeId:Rep[Int]=column[Int]("employee_id",O.PrimaryKey)

    def employeeName:Rep[String]=column[String]("employee_name")

    def employeeMobile:Rep[String]=column[String]("employee_mobile")

    def employeeEmail:Rep[String]=column[String]("employee_email")

    def employeeAddress:Rep[String]=column[String]("employee_address")

    def employeeUsername:Rep[String]=column[String]("employee_username")

    def employeePassword:Rep[String]=column[String]("employee_password")

    override def * = (employeeId,employeeName,employeeMobile,employeeEmail,employeeAddress,employeeUsername,employeePassword)<>((Employee.apply _).tupled,Employee.unapply)
  }


  private val employeeTable =TableQuery[EmployeeTable]

  def insertEmployee(data:Employee):Future[Employee]={
    db.run(DBIO.seq(employeeTable+=data)) recover{
      case t:Throwable=>
        println("Error while inserting data to mysql: "+t.getLocalizedMessage)
        throw t
       }  map(_=> data)
  }

  def updateEmployee(data:Employee):Future[Int]={
    db.run(employeeTable.filter(_.employeeId===data.employee_id).update(data))
  }

  def filterByIdQuery(employee_id:Int): Query[EmployeeTable, Employee, Seq] =employeeTable.filter(_.employeeId===employee_id)

  def filterByNameQuery(employee_name:String): Query[EmployeeTable, Employee, Seq] =employeeTable.filter(_.employeeName===employee_name)

  def findByName(employee_name:String):Future[Seq[Employee]]={
    db.run(filterByNameQuery(employee_name).result).map{ data=>data}
  }

  def findById(employee_id:Int):Future[Seq[Employee]]={
    db.run(filterByIdQuery(employee_id).result).map{data=>data}
  }

  def deleteEmployeeById(employee_id:Int):Future[Int]={
    db.run(filterByIdQuery(employee_id).delete)
  }

  def listAllEmployees():Future[Seq[Employee]]={
    db.run(employeeTable.result)
  }
}
