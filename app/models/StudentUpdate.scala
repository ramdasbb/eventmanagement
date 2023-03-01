package models

import play.api.libs.json.{Json, OFormat}

case class StudentUpdate(student: Student,message:String)

object StudentUpdate {
  implicit val format: OFormat[StudentUpdate] = Json.format[StudentUpdate]
}