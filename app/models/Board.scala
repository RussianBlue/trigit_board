package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._

case class Board(id:Option[Long],
                 user_id: String,
                 project_id:Long,
                 board_category_id : Long,
                 title:String,
                 body:String,
                 readings:Long,
                 created_at:Option[Date],
                 updated_at:Option[Date],
                 file_name:String,
                 file_content_type:Option[String],
                 file_size:Long,
                 file_updated_at:Option[Date],
                 parent_id:Long)

object Board extends Table[Board]("boards") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id = column[String]("user_id")
  def project_id = column[String]("email")
  def board_category_id = column[String]("name")
  def title = column[String]("password")
  def body = column[String]("user_type")
  def readings = column[String]("project_id")
  def created_at = column[String]("user_id")
  def updated_at = column[String]("email")
  def file_name = column[String]("name")
  def file_content_type = column[String]("password")
  def file_size = column[String]("user_type")
  def file_updated_at = column[String]("project_id")
  def parent_id = column[String]("project_id")
  
  val simple = {
    get[Pk[Long]]("boards.id") ~
    get[String]("boards.user_id") ~
    get[Long]("boards.project_id") ~
    get[Long]("boards.board_category_id") ~
    get[String]("boards.title") ~
    get[String]("boards.body") ~
    get[Long]("boards.readings") ~
    get[Option[Date]]("boards.created_at") ~
    get[Option[Date]]("boards.updated_at") ~
    get[String]("boards.file_name") ~
    get[Option[String]]("boards.file_content_type") ~
    get[Long]("boards.file_size") ~
    get[Option[Date]]("boards.file_updated_at") ~ 
    get[Long]("boards.parent_id") map {
      case id~user_id~project_id~board_category_id~title~body~readings~created_at~updated_at~file_name~file_content_type~file_size~file_updated_at~parent_id => Board(
        id, user_id, project_id, board_category_id, title, body, readings, created_at, updated_at, file_name, file_content_type, file_size, file_updated_at, parent_id
      )
    }
  }

  def findAll: Seq[Board] = {
    DB.withConnection { implicit connection =>
      SQL("select * from boards").as(Board.simple *)
    }
  }

  def findByProject(id:Long):Option[Board] = {
    DB.withConnection { implicit connection =>      
        SQL("select * from boards where project_id = {project_id}").on(
          'project_id -> id
        ).as(Board.simple.singleOpt)
     }
  }

  //해당 카테고리의 리스트 가져오기
  def list(id:Long, page:Long = 0, pageSize:Long = 10): Seq[Board] = {
    val offset = pageSize * page

    DB.withConnection { implicit connection =>
      SQL(
        """
        select * from boards where project_id = {board_category_id}
        order by id desc
        limit {pageSize} offset {offset}
        """
      ).on(
        'board_category_id -> id,
        'page -> page,
        'pageSize -> pageSize,
        'offset -> offset
      ).as(Board.simple.*)
    }
  }

  def getCountCategory(id:Long): Long = {
    DB.withConnection { implicit connection =>
      SQL(
        """
        select count(*) from boards where project_id = {project_id}
        """
      ).on(
        'project_id -> id
      ).as(scalar[Long].single)
    }
  }

  /**
   * Check if a user is a member of this project
   */
  def isMember(project: Long, user: String): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select count(user.email) = 1 from user
          join project_member on project_member.user_email = user.email
          where project_member.project_id = {project} and user.email = {email}
        """
      ).on(
        'project -> project,
        'email -> user
      ).as(scalar[Boolean].single)
    }
  }

  def create(boards:Board):Board = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into boards values (
            {id}, {user_id}, {project_id}, {board_category_id}, {title}, {body}, {readings}, {created_at}, {updated_at}, {file_name}, {file_content_type}, {file_size}, {file_updated_at}, {parent_id}
          )
        """
      ).on(
        'id -> boards.id,
        'user_id -> boards.user_id,
        'project_id -> boards.project_id,
        'board_category_id -> boards.board_category_id,
        'title -> boards.title,
        'body -> boards.body,
        'readings -> boards.readings,
        'created_at -> boards.created_at,
        'updated_at -> boards.updated_at,
        'file_name -> boards.file_name,
        'file_content_type -> boards.file_content_type,
        'file_size -> boards.file_size,
        'file_updated_at -> boards.file_updated_at,
        'parent_id -> boards.parent_id
      ).executeUpdate()

      boards.copy(id = boards.id)
    }
  }

  def delete(id:Long) {
    DB.withConnection { implicit connection =>
      SQL("delete from boards where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }

  def update(id:Long) {
    val newName = "전채윤"
    DB.withConnection { implicit connection =>
      SQL("update boards set body = {body} where id = {id}").on(
        'id -> id, 'body -> newName
      ).executeUpdate()
    }
  }

  def increaseReading(id:Long, count:Long){
    val newCount:Long = count + 1
    DB.withConnection { implicit connection =>
      SQL("update boards set readings = {readings} where id = {id}").on(
        'id -> id, 'readings -> newCount
      ).executeUpdate()
    }
  }
}