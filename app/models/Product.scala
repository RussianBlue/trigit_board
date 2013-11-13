package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Product(id:Pk[Long], 
                   project_id:String,
                   chapter:Long,
                   ms_clip_file:String,
                   ms_file_type:Option[String],
                   sb_clip_file:String,
                   sb_file_type:Option[String],
                   lesson_url:String,
                   lesson_width:String,
                   lesson_height:String,
                   created_at:Option[Date],
                   updated_at:Option[Date])

object Product {

  val simple = {
    get[Pk[Long]]("products.id") ~    
    get[String]("products.project_id") ~
    get[Long]("products.chapter") ~
    get[String]("products.ms_clip_file")~
    get[Option[String]]("products.ms_file_type")~
    get[String]("products.sb_clip_file")~
    get[Option[String]]("products.sb_file_type")~
    get[String]("products.lesson_url")~
    get[String]("products.lesson_width")~
    get[String]("products.lesson_height")~
    get[Option[Date]]("products.created_at")~
    get[Option[Date]]("products.updated_at") map {
      case id~project_id~chapter~ms_clip_file~ms_file_type~sb_clip_file~sb_file_type~lesson_url~lesson_width~lesson_height~created_at~updated_at => Product(
        id, project_id, chapter, ms_clip_file, ms_file_type, sb_clip_file, sb_file_type, lesson_url, lesson_width, lesson_height, created_at, updated_at
      )
    }
  }

  def create(products:Product) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into products values (
            {id}, {project_id}, {chapter}, {ms_clip_file}, {ms_file_type}, {sb_clip_file}, {sb_file_type}, {lesson_url}, {lesson_width}, {lesson_height}, {created_at}, {updated_at}
          )
        """
      ).on(
        'id -> products.id,
        'project_id -> products.project_id,
        'chapter -> products.chapter,
        'ms_clip_file -> products.ms_clip_file,
        'ms_file_type -> products.ms_file_type,
        'sb_clip_file -> products.sb_clip_file,
        'sb_file_type -> products.sb_file_type,
        'lesson_url -> products.lesson_url,
        'lesson_width -> products.lesson_width,
        'lesson_height -> products.lesson_height,
        'created_at -> products.created_at,
        'updated_at -> products.updated_at
      ).executeUpdate()

      products.copy(id = products.id)
    }
  }

  def findByProduct(project_id:String):Seq[Product] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
        select * from products where project_id = {project_id}
        order by chapter
        """
        ).on(
        'project_id -> project_id
      ).as(Product.simple.*)
    }
  }

  def findBySameChapter(project_id:String, chapter:String):Long = {
     DB.withConnection { implicit connection =>
      SQL(
        """
        select count(*) from products where project_id = {project_id} and chapter = {chapter}
        """
      ).on(
        'project_id -> project_id,
        'chapter -> chapter
      ).as(scalar[Long].single)
    }
  }

  /*
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
  }*/
}
