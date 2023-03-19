package models

import play.api.libs.json.{Json, OFormat}

case class SearchResponse(message: String, content: Seq[Employee])

object SearchResponse {
  implicit val format: OFormat[SearchResponse] = Json.format[SearchResponse]
}
