package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.io.File

import anorm._

import models._
import views._

object Application extends Controller with Secured{

  def main = IsAuthenticated { user => _ =>
    User.findByUserId(user).map { user =>
      Ok(views.html.content(user))
    }.getOrElse(Forbidden)
  }

  def download(_url:String, _fileName:String) = Action {
    Ok.sendFile(new java.io.File(_url), fileName = (name) => _fileName)
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Boards.list, 
        Boards.newBoard, 
        Boards.readBoard, 
        Boards.create, 
        Boards.update, 
        Boards.remove, 
        Admin.newUser, 
        Admin.editUser, 
        Admin.removeUser, 
        Projects.newProject, 
        Projects.removeProject, 
        Projects.editProject
      )
    ).as("text/javascript")
  }
}