package models


import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Project(id:Pk[Long], 
                   project_id:String, 
                   project_title:String,
                   created_at:Option[Date])

object Project {

	val simple = {
		get[Pk[Long]]("projects.id") ~		
		get[String]("projects.project_id") ~
		get[String]("projects.project_title") ~
    get[Option[Date]]("projects.created_at") map {
      case id~project_id~project_title~created_at => Project(
        id, project_id, project_title, created_at
      )
    }
	}

	def newProject(projects:Project) = {
		DB.withConnection { implicit connection =>
			SQL(
				"""
					insert into projects values (
            {id}, {project_id}, {project_title}, {created_at}
          )
        """
      ).on(
        'id -> projects.id,
        'project_id -> projects.project_id,
        'project_title -> projects.project_title,
        'created_at -> projects.created_at
      ).executeUpdate()

      projects.copy(id = projects.id)
		}
	}

  def edit(id:Long, project_title:String) {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update projects set project_title = {project_title} where id = {id}
        """
      ).on(
        'id -> id, 'project_title -> project_title
      ).executeUpdate()
    }
  }

  def remove(id:Long) {
    DB.withConnection { implicit connection =>
      SQL("delete from projects where id = {id}").on(
        'id -> id
      ).executeUpdate()
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

  def findByProject(id:Long):Seq[Project]= {
    DB.withConnection { implicit connection =>
      SQL("select * from projects where id = {id}").on(
        'id -> id
      ).as(Project.simple.*)
    }
  }

  def findByProjectId(project_id: String): Seq[Project] = {
    DB.withConnection { implicit connection =>
      SQL("select * from projects where project_id = {project_id}").on(
        'project_id -> project_id
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
