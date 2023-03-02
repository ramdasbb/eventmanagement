package dao

import models.Employee

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  // (id: Long, name: String, mobile: String,email: String, address: String,username: String,password:String)

  class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employee") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")
    def mobile: Rep[String] = column[String]("mobile")
    def email: Rep[String] = column[String]("email")
    def address: Rep[String] = column[String]("address")
    def username: Rep[String] = column[String]("username")
    def password: Rep[String] = column[String]("password")

    def * :ProvenShape[Employee] = (id, name, mobile, email, address, username, password) <> ((Employee.apply _).tupled, Employee.unapply)
  }

  private val employeeQuery = TableQuery[EmployeeTable]

  def getAll: Future[Seq[Employee]] = db.run(employeeQuery.result)

  def getById(id: Long): Future[Option[Employee]] = db.run(employeeQuery.filter(_.id === id).result.headOption)

  def add(employee: Employee): Future[Employee] =
    db.run(employeeQuery returning employeeQuery.map(_.id) into ((emp, id) => emp.copy(id = id)) += employee)

  def update(employee: Employee): Future[Int] = db.run(employeeQuery.filter(_.id === employee.id).update(employee))

  def updateById(id: Long, updatedEmployee: Employee): Future[Int] = {
    val query = employeeQuery.filter(_.id === id)
    val action = query.update(updatedEmployee)
    db.run(action)
  }


  def delete(id: Long): Future[Int] = db.run(employeeQuery.filter(_.id === id).delete)

  def filterQuery(id: Long): Query[EmployeeTable, Employee, Seq] = employeeQuery.filter(_.id === id)

  def filterByNameQuery(name: String): Query[EmployeeTable, Employee, Seq] = employeeQuery.filter(_.name === name)

  def findByName(name: String): Future[Seq[Employee]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def findById(id: Long) : Future[Seq[Employee]] = {
    db.run( filterQuery(id).result ).map{
      dataFromDb =>
        println("Data is >>>>"+ dataFromDb)
        dataFromDb
    }
  }
}

