package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Board(id: Pk[Long] = NotAssigned,
                 email: String,
                 name:String,
                 board_category_id : Long,
                 title:String,
                 body:String,
                 readings:Long,
                 created_at:Option[Date],
                 updated_at:Option[Date],
                 clip_file_name:String,
                 clip_file_content_type:String,
                 clip_file_size:Long,
                 clip_file_updated_at:Option[Date])

object Board {
  val simple = {
    get[Pk[Long]]("boards.id") ~
    get[String]("boards.email") ~
    get[String]("boards.name") ~
    get[Long]("boards.board_category_id") ~
    get[String]("boards.title") ~
    get[String]("boards.body") ~
    get[Long]("boards.readings") ~
    get[Option[Date]]("boards.created_at") ~
    get[Option[Date]]("boards.updated_at") ~
    get[String]("boards.clip_file_name") ~
    get[String]("boards.clip_file_content_type") ~
    get[Long]("boards.clip_file_size") ~
    get[Option[Date]]("boards.clip_file_updated_at") map {
      case id~email~name~board_category_id~title~body~readings~created_at~updated_at~clip_file_name~clip_file_content_type~clip_file_size~clip_file_updated_at => Board(
        id, email, name, board_category_id, title, body, readings, created_at, updated_at, clip_file_name, clip_file_content_type, clip_file_size, clip_file_updated_at
      )
    }
  }

  def findAll: Seq[Board] = {
    DB.withConnection { implicit connection =>
      SQL("select * from boards").as(Board.simple *)
    }
  }
  //아이디 검색
  def findById(id:Long): Option[Board] = {
    DB.withConnection { implicit connection =>
      SQL("select * from boards where id = {id}").on(
        'id -> id
      ).as(Board.simple.singleOpt)
    }
  }
  //이메일 검색
  def findByEmail(email: String): Option[Board] = {
    DB.withConnection { implicit connection =>
      SQL("select * from boards where email = {email}").on(
        'email -> email
      ).as(Board.simple.singleOpt)
    }
  }

  //해당 카테고리의 리스트 가져오기
  def list(id:Long, page:Long = 0, pageSize:Long = 10): Seq[Board] = {
    val offset = pageSize * page

    DB.withConnection { implicit connection =>
      SQL(
        """
        select * from boards where board_category_id = {board_category_id}
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
        select count(*) from boards where board_category_id = {board_category_id}
        """
      ).on(
        'board_category_id -> id
      ).as(scalar[Long].single)
    }
  }

//  def getCountCategory(id:Long): Seq[Board] = {
//    DB.withConnection { implicit connection =>
//      SQL(
//        """
//          select * from boards where board_category_id = {board_category_id}"
//          select count(*) from boards where board_category_id
//        """
//      ).on(
//        'board_category_id -> id
//      ).as(Board.simple.*)
//    }
//  }

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
            {id}, {email}, {name}, {board_category_id}, {title}, {body}, {readings}, {created_at}, {updated_at}, {clip_file_name}, {clip_file_content_type}, {clip_file_size}, {clip_file_updated_at}
          )
        """
      ).on(
        'id -> boards.id,
        'email -> boards.email,
        'name -> boards.name,
        'board_category_id -> boards.board_category_id,
        'title -> boards.title,
        'body -> boards.body,
        'readings -> boards.readings,
        'created_at -> boards.created_at,
        'updated_at -> boards.updated_at,
        'clip_file_name -> boards.clip_file_name,
        'clip_file_content_type -> boards.clip_file_name,
        'clip_file_size -> boards.clip_file_size,
        'clip_file_updated_at -> boards.clip_file_updated_at
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