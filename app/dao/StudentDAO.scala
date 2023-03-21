package dao

import models.Student
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StudentDAO  @Inject() (dbConfigProvider:DatabaseConfigProvider)(implicit  ec:ExecutionContext)
{
  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

    //This code snippet is importing all the members of an object named dbConfig
  import dbConfig._

  /**
  This code snippet is importing the api object of the Slick library's profile package.
   The api object contains the core components of Slick, such as the Database class,
    the Table class, and the Query class.
   **/
  import profile.api._

  /**
   * The StudentTable class takes a single parameter, tag, which is of type Tag. The Tag parameter is required by Slick and is used to identify a specific table in the database.
  The Table class is a core component of Slick that represents a database table and its schema.
   **/
  class StudentTable(tag:Tag) extends Table[Student](tag,"student")
  {
    def id: Rep[Long] = column[Long]("id",O.PrimaryKey,O.AutoInc)

    def name:Rep[String]= column[String]("name")

    def mobile:Rep[Long]=column[Long]("mobile")

    def email:Rep[String]=column[String]("email")

    def college:Rep[String]=column[String]("college")

    def department:Rep[String]=column[String]("department")

    /**
     *  The <> method is then used to map this tuple of columns to an instance of the Student case class. The first argument to <> is a function that takes the tuple of columns and constructs an instance of the Student case class using the apply method of the Student companion object. The second argument to <> is a function that takes an instance of the Student case class and returns a tuple of values that can be inserted into the database.
     *  The tupled method is used to convert the apply method of the Student companion object to a function that takes a tuple of arguments. This is required because the <> method expects a function that takes a tuple of columns as its argument.
     *  In summary, this line of code defines the mapping between the database table columns and the corresponding fields in a Scala case class, which allows Slick to read and write data to and from the database using the case class
     */
    override def * = (id,name,mobile,email,college,department)<>((Student.apply _).tupled,Student.unapply)
  }


  /**
   * creates an instance of the TableQuery class that is used to interact with a database table named students.
   * TableQuery is a class in Slick that is used to represent a table in a database. It is a type-safe way to interact with a table because it allows you to specify the table schema using a Scala class, and provides type-safe methods to perform various operations on the table.
   *
   *  In this line of code, the StudentTable class represents the schema of the students table in the database. TableQuery[StudentTable] creates a new instance of the TableQuery class, which is parameterized by the StudentTable class. This means that the new instance of TableQuery represents the students table in the database, and provides type-safe methods to interact with the table.
   *
   *  The private modifier means that the students instance is only accessible within the scope of the class where it is defined. This is a common way to encapsulate the details of database access within a class, and to ensure that the database interactions are only performed in a controlled way.
   *
   *  In summary, the line of code private val students=TableQuery[StudentTable] creates an instance of the TableQuery class that represents the students table in the database, and provides type-safe methods to interact with the table. The private modifier ensures that the instance is only accessible within the scope of the class where it is defined.
   */
  private val studentTable=TableQuery[StudentTable]


  /**
  This code is a method definition in Scala Slick that inserts a new row into a database table represented by the student object, and returns a Future of the Student object that was inserted.
This means that the method will run asynchronously and return a Student object when it completes.
   */
  def insertStudent(data:Student):Future[Student]={

    /**
     * This line performs the actual insertion of data into the database table.
     * The db.run() method is used to execute a Slick database action that is defined as a DBIO object.
     * In this case, the action is a sequence of database operations that contains a single += operation,
     * which adds the data object to the student object (which represents a database table) in the database.
     */
      db.run(
        DBIO.seq(
          studentTable+=data
           )
      ) recover{             /**This block of code is used to handle any errors that occur during the execution of the database action. If an exception is thrown, the recover method is called with a partial function that matches on any Throwable object. In this case, the error is printed to the console and re-thrown to be handled by the caller of the insertStudent method.*/
        case t: Throwable=>
          println("Error is "+t.getLocalizedMessage)
          throw t
      } map(_=>data)
  }


  /**
  method called update that takes a single parameter of type Student,
  and returns a Future of type Int. This means that the method will run asynchronously
  and return an integer representing the number of rows that were updated when it completes.
   */
  def update(data:Student):Future[Int]={
    db.run(studentTable.filter(_.id===data.id).update(data))
  }


  def filterByIdQuery(id:Long): Query[StudentTable, Student, Seq] =studentTable.filter(_.id===id)

  def filterByNameQuery(name:String):Query[StudentTable,Student,Seq]=studentTable.filter(_.name===name)


  def findByName(name:String):Future[Seq[Student]]={
    db.run(filterByNameQuery(name).result).map{
      dataFromDB=>
        println("Data is >>>>"+dataFromDB)
        dataFromDB
    }
  }

  def findById(id:Long):Future[Seq[Student]]={
    db.run(filterByIdQuery(id).result).map{
      x=>println("Data is >>>"+x)
      x
    }
  }

  def retrieveStudentById(id:Long):Future[Option[Student]]={
    db.run(studentTable.filter(_.id===id).result.headOption)
  }

  def deleteStudentById(id:Long):Future[Int]={
    db.run(filterByIdQuery(id).delete)
  }

  def listAllStudents():Future[Seq[Student]]={
    db.run(studentTable.result)
  }
}
