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

import anorm._

import models._
import views._

/**
 * Manage tasks related operations.
 */
object Admin extends Controller with Secured {

	 val writeForm = Form(
    tuple(
      "title" -> text,
      "lesson" -> text
    )
  )

	 //프로젝트 생성 페이지로 이동
	def createProjectPage = IsAuthenticated { user => _ =>
		User.findById(user).map
		{
			user=>
			Ok(views.html.admin.new_project(user))
		}.getOrElse(Forbidden)
	}

	//유저 정보 보기 
	def viewUserList(page:Long) = IsAuthenticated { user => _ =>
		User.findById(user).map
		{
			user=>			
      val pageLength = 10
      val users = User.findAll(page - 1, pageLength)
      val count = User.findAllUserCount

			Ok(views.html.admin.user_list(user, users, page, pageLength, count))
		}.getOrElse(Forbidden)
	}

	//프로젝트 리스트 보기
	def viewProjectList(page:Long) = IsAuthenticated { user => _ =>
		User.findById(user).map
		{
			user=>			
      val pageLength = 10
      val projects = Project.findAll(page - 1, pageLength)
      val count = Project.findAllProjectCount

			Ok(views.html.admin.project_list(user, projects, page, pageLength, count))
		}.getOrElse(Forbidden)
	}

	def newProject = Action {
		Ok
	}
}