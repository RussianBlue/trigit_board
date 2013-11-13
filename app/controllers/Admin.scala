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

import java.io.File

import anorm._

import models._
import views._

/**
 * Manage tasks related operations.
 */
object Admin extends Controller with Secured {
	//유저 정보 보기
	def viewUserList(page:Long) = IsAuthenticated { user => _ =>
		User.findByUserId(user).map
		{
			user=>
			//보여지는 페이지 갯수
      val pageLength = 10
      val users = User.findAll(page - 1, pageLength)
      //유저 카운트 갯수
      val count = User.findAllUserCount

			Ok(views.html.admin.user_list(user, users, page, pageLength, count))
		}.getOrElse(Forbidden)
	}
	//신규 유저 생성 페이지로 이동
	def newUserPage = IsAuthenticated { user => _ =>
		User.findByUserId(user).map
		{
			user =>
			Ok(views.html.admin.new_user(user, User.findAllUser, Project.findAllProject))			
		}.getOrElse(Forbidden)
	}
	//신규 유저 생성
	def newUser(user_id:String, email:String, name:String, password:String, user_type:String, project_id:String) = Action { implicit request =>		
		val users = User.create(User(NotAssigned, user_id, email, name, password, user_type, project_id))
		Redirect(routes.Admin.viewUserList(1))
	}

	//유저 정보 수정 페이지로 이동
	def editUserPage(id:Long) = IsAuthenticated { user => _ =>
		User.findByUserId(user).map
		{
			user =>
			Ok(views.html.admin.edit_user(user, User.findById(id), Project.findAllProject))
		}.getOrElse(Forbidden)
	}

	//유저 수정
	def editUser(id:Long, user_id:String, email:String, name:String, password:String, user_type:String, project_id:String) = IsAuthenticated { _ => implicit request =>		
		User.editUser(id, user_id, email, name, password, user_type, project_id)
    Ok
	}
	//유저 삭제
	def removeUser(id:Long) = IsAuthenticated { user => _ =>
		User.removeUser(id)
		Ok
	}
}