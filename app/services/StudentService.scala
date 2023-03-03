package services

import dao.StudentDAO

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudentService @Inject() (studentDAO:StudentDAO)
{
    def findAndDeleteStudent(name:String):Future[Int]={
      studentDAO.findByName(name)
        .flatMap{s=>
        s.find(_.name==name) match {
          case Some(value)=>studentDAO.deleteStudentById(value.id)
          case None=>throw new Exception(s"Student not found by name $name")
        }
      }
    }
}
