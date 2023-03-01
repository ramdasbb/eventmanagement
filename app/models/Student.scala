package models

import play.api.libs.json.{Json, OFormat}

case class Student(studentId: Long, fullName: String, collegeName: String, department:String)

object Student {
  implicit val format: OFormat[Student] = Json.format[Student]
}
