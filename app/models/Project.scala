package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Project(email: String, name: String, password: String)

object Project {

}