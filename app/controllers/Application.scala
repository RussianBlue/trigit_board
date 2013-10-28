package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._

import models._
import views._

object Application extends Controller with Secured{

  def main = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      Ok(views.html.main(views.html.project.project(), views.html.content()))
    }.getOrElse(Forbidden)
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Boards.list, Boards.newBoard, Boards.readBoard, Boards.create, Boards.update, Boards.remove
      )
    ).as("text/javascript")
  }
}