import dao.GenericDAO

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class GenericDaoImpl[T](implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider)
  extends GenericDAO[T] {

  protected val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val tableQuery = TableQuery[Table[_]]

  def create(model: T): Future[T] = {
    val insertAction = tableQuery returning tableQuery += model
    db.run(insertAction).map(_ => model)
  }

  def findById(id: Long): Future[Option[T]] =
    db.run(tableQuery.filter(_.id === id).result.headOption)


  private def rowToModel(row: Any): T = {
    row match {
      case (t: T) => t
      case _ => throw new RuntimeException("Unexpected row type")
    }
  }

  def findAll(): Future[Seq[T]] = {
    val selectAction = tableQuery.result
    db.run(selectAction).map(_.map(row => rowToModel(row)))
  }

  def update(model: T): Future[T] =
    db.run(tableQuery.update(model)).map(_ => model)

  def delete(id: Long): Future[Unit] =
    db.run(tableQuery.filter(_.id === id).delete).map(_ => ())
}
