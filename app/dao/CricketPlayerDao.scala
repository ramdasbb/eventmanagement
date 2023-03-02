package dao

import models.CricketPlayer

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CricketPlayerDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val cricketTeamDao: CricketTeamDao)
                         (implicit executionContext: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CricketPlayerTable(tag: Tag) extends Table[CricketPlayer](tag, "PLAYER") {
    def id = column[Long]("id", O.AutoInc)
    def name = column[String]("name", O.PrimaryKey)
    def score = column[Int]("score")
    def team_name = column[String]("team_name")
    def team = foreignKey("team_fk", team_name, cricketTeamDao.cricketTeams)(_.name)
    def * = (id, name, score, team_name) <> ((CricketPlayer.apply _).tupled, CricketPlayer.unapply)
  }

  val cricketPlayers = TableQuery[CricketPlayerTable]

  def add(player: CricketPlayer): Future[CricketPlayer] = db.run {
    (cricketPlayers.map(p => (p.name, p.score, p.team_name))
      returning cricketPlayers.map(_.id)
      into ((data, id) => CricketPlayer(id, data._1, data._2, data._3))
      ) += (player.name, player.score, player.team_name)
  }

  def getByTeamId(id: Long): Future[Option[CricketPlayer]] = db.run {
    cricketPlayers.filter(_.id === id).result.headOption
  }

  def playersByTeam(team_name: String): Future[Seq[CricketPlayer]] = db.run {
    cricketPlayers.filter(_.team_name === team_name).result
  }

  def findById(id: Long): Future[Option[CricketPlayer]] = db.run {
    cricketPlayers.filter(_.id === id).result.headOption
  }


  def list(): Future[Seq[CricketPlayer]] = db.run {
    cricketPlayers.result
  }

  def update(cricketPlayer: CricketPlayer): Future[Int] = db.run {
    cricketPlayers.filter(_.id === cricketPlayer.id).update(cricketPlayer)
  }


  def filterQuery(id: Long): Query[CricketPlayerTable,CricketPlayer, Seq] = cricketPlayers.filter(_.id === id)

  def filterByNameQuery(name: String): Query[CricketPlayerTable,CricketPlayer, Seq] = cricketPlayers.filter(_.name === name)

  def findByName(name: String): Future[Seq[CricketPlayer]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def delete(id: Long): Future[Int] = {
    val pp = db.run(filterQuery(id).delete)
    pp
  }

}