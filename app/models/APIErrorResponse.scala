package models

import play.api.libs.json.{Json, OFormat}

case class APIErrorResponse(status:String,message:String)

object APIErrorResponse{
  implicit val format:OFormat[APIErrorResponse]=Json.format[APIErrorResponse]
}

