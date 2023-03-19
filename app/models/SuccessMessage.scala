package models

import play.api.libs.json.{Json, OFormat}

case class SuccessMessage(message: String)

object SuccessMessage {
  implicit val format: OFormat[SuccessMessage] = Json.format[SuccessMessage]
}
