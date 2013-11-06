package models


import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Project(id:Pk[Long], 
                   project_id:Long, 
                   project_title:String, 
                   lesson_id:Long, 
                   lesson_title:String, 
                   created_at:Option[Date])

object Project {

	val simple = {
		get[Pk[Long]]("projects.id") ~		
		get[Long]("projects.project_id") ~
		get[String]("projects.project_title") ~
		get[Long]("projects.lesson_id")~
    get[String]("projects.lesson_title")~
    get[Option[Date]]("projects.created_at") map {
      case id~project_id~project_title~lesson_id~lesson_title~created_at => Project(
        id, project_id, project_title, lesson_id, lesson_title, created_at
      )
    }
	}

	def newProject(projects:Project) = {
		DB.withConnection { implicit connection =>
			SQL(
				"""
					insert into projects values (
            {id}, {project_id}, {project_title}, {lesson_id}, {lesson_title}, {created_at}
          )
        """
      ).on(
        'id -> projects.id,
        'project_id -> projects.project_id,
        'project_title -> projects.project_title,
        'lesson_id -> projects.lesson_id,
        'lesson_title -> projects.lesson_title,
        'created_at -> projects.created_at
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

  def findAllProject:Seq[Project] = {
    DB.withConnection { implicit connection =>
      SQL("select * from projects").as(Project.simple.*)
    }
  }

  def findAllProjectCount:Long = {
    DB.withConnection { implicit connection =>
      SQL("select count(*) from projects").as(scalar[Long].single)
    }
  }
}
