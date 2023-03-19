package dao

import models.Employee
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employee") {

    def employeeId:Rep[Int] = column[Int]("employeeId", O.PrimaryKey)
    def employeeName:Rep[String] = column[String]("employeeName")
    def employeeMobile:Rep[String] = column[String]("employeeMobile")
    def employeeEmail: Rep[String] = column[String]("employeeEmail", O.Unique)
    def employeeAddress: Rep[String] = column[String]("employeeAddress")
    def employeeUsername: Rep[String] = column[String]("employeeUsername", O.Unique)
    def employeePassword: Rep[String] = column[String]("employeePassword")

    def * = (employeeId, employeeName,employeeMobile,employeeEmail, employeeAddress, employeeUsername, employeePassword) <> ((Employee.apply _).tupled, Employee.unapply)
  }

  private val employeeTable = TableQuery[EmployeeTable]

  def filterQuery(id: Int): Query[EmployeeTable, Employee, Seq] = employeeTable.filter(_.employeeId === id)

  def searchQueryByUsername(name:String): Query[EmployeeTable, Employee, Seq] = employeeTable.filter(_.employeeUsername === name)

  def save(t: Employee): Future[Employee] = {
    db.run(DBIO.seq(employeeTable+=t)) recover {
      case t: Throwable =>
        println("Error while inserting data: " + t.getLocalizedMessage)
        throw t
    } map { _ => t }
  }

  def update(t: Employee): Future[Int] = {
    db.run(employeeTable.filter(_.employeeId === t.employee_id).update(t))
  }

  def delete(employeeId: Int): Future[Int] = {
    db.run(filterQuery(employeeId).delete)
  }

  def findById(employeeId: Int): Future[Seq[Employee]] = {
    db.run(filterQuery(employeeId).result).map{ data => data }
  }

  def findByName(userName: String): Future[Seq[Employee]] = {
    db.run(searchQueryByUsername(userName).result).map { data => data }
  }
}


