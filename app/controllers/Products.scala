package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.libs._

import java.util._
import java.text._
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

import java.util.Calendar
import java.io.File

import models._
import views._

/**
 * Manage tasks related operations.
 */
object Products extends Controller with Secured {
  //날짜 파싱

  val productForm = Form(
    tuple(
      "product_url" -> text,
      "product_width" -> text,
      "product_height" -> text
    )
  )

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)  

  def viewUserProject(project_id:String) = IsAuthenticated { user => _ =>
  	User.findByUserId(user).map { user=>
  		Ok(views.html.products.product_list(user, Project.findByProjectId(project_id), Product.findByProduct(project_id), productForm))
  	}.getOrElse(Forbidden)
  }

  def newProduct(project_id:String) = Action(parse.multipartFormData) { implicit request => 
    productForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      {
      	case(project_url, project_width, project_height) =>
      	//현재 시간
	      val today = Calendar.getInstance.getTime
	      //시간타입변환
	      val curTimeFormat = new SimpleDateFormat("yyyy-MM-dd")
	      //현재시간
	      val createDate = Some(date(curTimeFormat.format(today)))
	      val updateDate = Some(date(curTimeFormat.format(today)))

	      var msFileName:String = ""
	      var msFileType:Option[String] = Some("")
	      var sbFileName:String = ""
	      var sbFileType:Option[String] = Some("")
	      var chapter = "6"
	      var g_chapter = Product.findBySameChapter(project_id, chapter)

	      Logger.info(g_chapter.toString())
	      request.body.file("ms_file_name").map {
	      	ms_file =>
	      	msFileName = ms_file.filename
	      	msFileType = ms_file.contentType
	      	ms_file.ref.moveTo(new File("upload/" + project_id+"/ms/" + msFileName), true)
	      }

	      request.body.file("sb_file_name").map {
	      	sb_file =>
	      	sbFileName = sb_file.filename
	      	sbFileType = sb_file.contentType
	      	sb_file.ref.moveTo(new File("upload/" + project_id+"/ms/" + sbFileName), true)
	      }

	      if(g_chapter < 1){
	      	val products =  Product.create(
	        Product(NotAssigned, project_id, chapter.toLong, msFileName, msFileType, sbFileName, sbFileType, project_url, project_width, project_height, createDate, updateDate)
	      	)
	      }      
	      Redirect(routes.Products.viewUserProject(project_id))
      }
  	)
  }
}