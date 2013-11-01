package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Project(id:Pk[Long], user_id:String, project_id:Long, title:String, lesson:Long)

object Project {

	val simple = {
		get[Pk[Long]]("projects.id") ~
		get[String]("projects.user_id") ~
		get[Long]("projects.project_id") ~
		get[String]("projects.title") ~
		get[Long]("projects.lesson") map {
      case id~user_id~project_id~title~lesson => Project(
        id, user_id, project_id, title, lesson
      )
    }
	}

	def newProject(projects:Project) = {
		DB.withConnection { implicit connection =>
			SQL(
				"""
					insert into projects values (
            {id}, {user_id}, {project_id}, {title}, {lesson}
          )
        """
      ).on(
        'id -> projects.id,
        'user_id -> projects.user_id,
        'project_id -> projects.project_id,
        'board_category_id -> projects.title,
        'title -> projects.lesson
      ).executeUpdate()

      projects.copy(id = projects.id)
		}
	}

  /**
   * Retrieve all users.
   */
  def findAll(page:Long = 0, pageSize:Long = 10): Seq[Project] = {
    val offset = pageSize * page
    DB.withConnection { implicit connection =>
      SQL(
        """
        select * from projects
        order by id desc
        limit {pageSize} offset {offset}
        """
      ).on(
        'page -> page,
        'pageSize -> pageSize,
        'offset -> offset
      ).as(Project.simple.*)
    }
  }

  def findAllProjectCount:Long = {
    DB.withConnection { implicit connection =>
      SQL("select count(*) from projects").as(scalar[Long].single)
    }
  }
}
