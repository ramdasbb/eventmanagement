package models

import play.api.libs.json.{Json, OFormat}

case class APIResponse(data:Seq[Employee],message:String)

object APIResponse{
  implicit val format:OFormat[APIResponse]=Json.format[APIResponse]
}

