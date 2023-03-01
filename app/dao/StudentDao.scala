package dao

import models.Student
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.collection.View.Empty
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StudentDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  class StudentTable(tag: Tag) extends Table[Student](tag, "student") {

    /** The STUDENT ID column, which is the primary key, and auto incremented */
    def studentId:Rep[Long] = column[Long]("studentId", O.PrimaryKey)
    def fullName:Rep[String] = column[String]("fullName")
    def collegeName:Rep[String] = column[String]("collegeName")
    def department: Rep[String] = column[String]("department")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Person object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Person case classes
     * apply and unapply methods.
     */
    def * = (studentId, fullName, collegeName, department) <> ((Student.apply _).tupled, Student.unapply)
  }

  /**
   * The starting point for all queries on the people table.
   */
  private val student = TableQuery[StudentTable]

  /**
   * Create a student with the given fullName , collegeName and department.
   * This is an asynchronous operation, it will return a future of the created student, which can be used to obtain the
   * id for that student.
   */
  def insert(data: Student): Future[Student] = {
    db.run(
      DBIO.seq(
        student += data
      )
    ) recover {
      case t: Throwable =>
        println("ERROR IS " + t.getLocalizedMessage)
        throw t
    } map { _ =>
      data
    }
  }

  def update(data: Student): Future[Int] = {
    db.run(student.filter(_.studentId === data.studentId).update(data))
  }

  def filterQuery(id: Long): Query[StudentTable, Student, Seq] = student.filter(_.studentId === id)
  def filterByNameQuery(name: String): Query[StudentTable, Student, Seq] = student.filter(_.fullName === name)

  def findByName(name: String): Future[Seq[Student]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def findById(id: Long) : Future[Seq[Student]] = {
    db.run( filterQuery(id).result ).map{
      dataFromDb =>
        println("Data is >>>>"+ dataFromDb)
        dataFromDb
    }
  }

  def find2(id: Long): Future[Student] = {
    val pp = db.run(filterQuery(id).result.head)
    pp
  }

  def find(id: Long): Future[Student] = {
    val pp = db.run(student.filter(_.studentId === id).result.head)
    pp
  }

  def delete(id: Long): Future[Int] = {
    val pp = db.run(filterQuery(id).delete)
    pp
  }

  /**
   * List all the students in the database.
   */
  def list(): Future[Seq[Student]] = db.run {
    student.result
  }
}
