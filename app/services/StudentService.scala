package services

import com.google.inject.Inject
import com.google.inject.Singleton
import dao.StudentDao
import models.StudentUpdate
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class StudentService @Inject() (studentDao: StudentDao) {
        def findAndDelete(name: String): Future[Int] ={
                studentDao.findByName(name).flatMap { students =>
                        val foundStudent = students.find(_.fullName == name)
                        foundStudent match {
                                case Some(p) =>
                                        studentDao.delete(p.studentId)
                                case None => throw new Exception(s"student not found by name $name")
                        }
                }
        }
}
