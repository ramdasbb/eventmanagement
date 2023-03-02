package models

import play.api.libs.json.{Json, OFormat}

case class Audience(id: Long, name: String, guessed_score: Int, player_id: Long)

object Audience {
  implicit val audienceFormat: OFormat[Audience] = Json.format[Audience]
}