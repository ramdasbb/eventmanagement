package models

import play.api.libs.json.{Json, OFormat}

case class Employee(employee_id:Int,employee_name:String,employee_mobile:String,employee_email:String,employee_address:String,employee_username:String,employee_password:String)

object Employee {
  implicit val format: OFormat[Employee] = Json.format[Employee]
}