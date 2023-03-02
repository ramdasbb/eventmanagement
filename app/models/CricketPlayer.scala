package models

import play.api.libs.json.{Json, OFormat}

case class CricketPlayer(id: Long, name: String, score: Int, team_name: String)

object CricketPlayer {
  implicit val playerFormat: OFormat[CricketPlayer] = Json.format[CricketPlayer]
}