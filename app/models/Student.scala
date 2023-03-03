package models

import play.api.libs.json.{Json, OFormat}

case class Student(id:Long,name:String,mobile:Long,email:String,college:String,department:String)

object Student{
  implicit val format:OFormat[Student] =Json.format[Student]
}
