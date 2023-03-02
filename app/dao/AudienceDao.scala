package dao

import models.Audience

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AudienceDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cricketPlayerDao: CricketPlayerDao)(implicit executionContext: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class AudienceTable(tag: Tag) extends Table[Audience](tag, "Audience") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def guessed_score = column[Int]("guessed_score")

    def player_id = column[Long]("player_id")

    def player = foreignKey("player_fk", player_id, cricketPlayerDao.cricketPlayers)(_.id)

    def * = (id, name, guessed_score, player_id) <> ((Audience.apply _).tupled, Audience.unapply)
  }

  private val audience = TableQuery[AudienceTable]

  def add(data: Audience): Future[Audience] = db.run {
    (audience.map(p => (p.name, p.guessed_score, p.player_id))
      returning audience.map(_.id)
      into ((data, id) => Audience(id, data._1, data._2, data._3))
      ) += (data.name, data.guessed_score, data.player_id)
  }


  def getById(id: Long): Future[Option[Audience]] = db.run {
    audience.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Audience]] = db.run {
    audience.result
  }

  def update(id: Long, audienceToUpdate: Audience): Future[Unit] = {
    val audienceToUpdateRow = audienceToUpdate.copy(id)
    db.run(audience.filter(_.id === id).update(audienceToUpdateRow)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run(audience.filter(_.id === id).delete).map(_ => ())


}