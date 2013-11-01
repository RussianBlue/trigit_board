package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(id:Pk[Long] = NotAssigned, user_id:String, email: String, name: String, password: String, user_type:String, project_id:Long)

object User {

  // -- Parsers

  /**
   * Parse a User from a ResultSet
   */
  val simple = {
      get[Pk[Long]]("user.id") ~
      get[String]("user.user_id") ~
      get[String]("user.email") ~
      get[String]("user.name") ~
      get[String]("user.password") ~
      get[String]("user.user_type") ~
      get[Long]("user.project_id") map {
      case id~user_id~email~name~password~user_type~project_id => User(id, user_id, email, name, password, user_type, project_id)
    }
  }
  // -- Queries

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email
      ).as(User.simple.singleOpt)
    }
  }

  /**
    * Retrieve a User from id.
    */
  def findById(user_id:String): Option[User] = {
    DB.withConnection { implicit  connection =>
      SQL("select * from user where user_id = {user_id}").on(
        'user_id -> user_id
      ).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll(page:Long = 0, pageSize:Long = 10): Seq[User] = {
    val offset = pageSize * page
    DB.withConnection { implicit connection =>
      SQL(
        """
        select * from user
        order by id desc
        limit {pageSize} offset {offset}
        """
      ).on(
        'page -> page,
        'pageSize -> pageSize,
        'offset -> offset
      ).as(User.simple.*)
    }
  }

  def findAllUserCount:Long = {
    DB.withConnection { implicit connection =>
      SQL("select count(*) from user").as(scalar[Long].single)
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(user_id: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where
         user_id = {user_id} and password = {password}
        """
      ).on(
        'user_id -> user_id,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }

  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (
            {id}, {user_id}, {email}, {name}, {password}, {user_type}, {project_id}
          )
        """
      ).on(
        'id -> user.id,
        'user_id -> user.user_id,
        'name -> user.name,
        'password -> user.password,
        'type -> user.user_type,        
        'project_id -> user.project_id
      ).executeUpdate()

      user
    }
  }

}
