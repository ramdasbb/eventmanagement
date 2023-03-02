package models

import play.api.libs.json.{Json, OFormat}
case class Employee(id: Long, name: String, mobile: String,email: String, address: String,username: String,password:String)

object Employee {
  implicit val format: OFormat[Employee] = Json.format[Employee]
}