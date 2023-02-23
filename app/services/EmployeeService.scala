package services

import com.google.inject.Singleton
import dao.EmployeeDao

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class EmployeeService @Inject() (employeeDao: EmployeeDao) {
  def findAndDelete(name: String): Future[Int] ={
    employeeDao.findByName(name).flatMap { emp =>
      val foundEmployee = emp.find(_.name == name)
      foundEmployee match {
        case Some(e) =>
          employeeDao.delete(e.id)
        case None => throw new Exception(s"employee not found by name ${name}")
      }
    }
  }
}
