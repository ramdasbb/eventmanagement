package models

import play.api.libs.json.Json

/*
* - Name
        - Country
        - Team
        - Role
        - Team
        - Match many matches*/
case class Player(name: String, country: String, role: Seq[String]){

}

object Player {
  // I have to define the serialise and deserialise to read and write data.
  // We are using Json formatted data only
  implicit val format = Json.format[Player]

  var players = Seq[Player]()

  def addPlayer(player: Player): Seq[Player] = {
    players :+ player
  }

  def getPlayers = players

}