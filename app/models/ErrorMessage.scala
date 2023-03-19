package models

import play.api.libs.json.{Json, OFormat}

case class ErrorMessage(status: String = "error", message: String)

object ErrorMessage {
  implicit val format: OFormat[ErrorMessage] = Json.format[ErrorMessage]
}
