package dao

import scala.concurrent.Future

trait GenericDAO[T] {
  def create(model: T): Future[T]
  def findById(id: Long): Future[Option[T]]
  def findAll(): Future[Seq[T]]
  def update(model: T): Future[T]
  def delete(id: Long): Future[Unit]
}


