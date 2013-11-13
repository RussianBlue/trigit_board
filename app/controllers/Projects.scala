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
object Projects extends Controller with Secured {
	//날짜 파싱
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

	//프로젝트 생성 페이지로 이동
	def newProjectPage = IsAuthenticated { user => _ =>
		User.findByUserId(user).map
		{
			user=>
			Ok(views.html.admin.new_project(user))
		}.getOrElse(Forbidden)
	}
	//프로젝트 수정하기 페이지로 이동
	def editProjectPage(id:Long) = IsAuthenticated { user => _ =>
		User.findByUserId(user).map
		{
			user=>
			Ok(views.html.project.edit_project(user, Project.findByProject(id)))
		}.getOrElse(Forbidden)
	}

	 //프로젝트 리스트 보기
	def viewProjectList(page:Long) = IsAuthenticated { user => _ =>
		User.findByUserId(user).map
		{
			user=>			
      val pageLength = 10
      val projects = Project.findAll(page - 1, pageLength)
      val count = Project.findAllProjectCount

			Ok(views.html.admin.project_list(user, projects, page, pageLength, count))
		}.getOrElse(Forbidden)
	}
	
	//신규프로젝트 생성
	def newProject(project_id:String, project_title:String) = IsAuthenticated { user => _ =>
		User.findByUserId(user).map
		{
			user=>
			//현재 시간
      val today = Calendar.getInstance.getTime
      //시간타입변환
      val curTimeFormat = new SimpleDateFormat("yyyy-MM-dd")
      //현재시간 
      val createDate = Some(date(curTimeFormat.format(today)))

      val projects = Project.newProject(Project(NotAssigned, project_id, project_title, createDate))
      Redirect(routes.Projects.viewProjectList(1))
		}.getOrElse(Forbidden)
	}

	def editProject(id:Long, project_title:String) = IsAuthenticated { user => _ =>
		Project.edit(id, project_title)
		Ok
	}

	def removeProject(id:Long) = IsAuthenticated { user => _ =>
		Project.findByProject(id).map {
			projects =>
			var project_id = projects.project_id.mkString("")

			User.findByProject(project_id).map{
				user_project =>				
				
				var __id:String = user_project.project_id				
				var __arr:Array[String] = __id.split('|')

				for(i <- __arr.indices) {
				  if(__arr(i) == projects.project_id){
				  	User.editProject(user_project.id.get, __arr.take(i).mkString("|"))
				  }
				}
			}
		}
		Project.remove(id)
		Ok
	}
}	