package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

import play.api.db.DB
import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import Database.threadLocalSession


object Login extends Controller {
  /**
   * Login page.
   */

  lazy val database = Database.forDataSource(DB.getDataSource())

  def login = Action { implicit request =>
    Ok(views.html.signup.login(loginForm))
  }

  val loginForm = Form(
    tuple(
      "id" -> text,
      "password" -> text
    ) verifying ("ID와 Password를 확인해 주세요.", result => result match {
      case (id, password) => {
        User.authenticate(id, password).isDefined
      }
    })
  )

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.signup.login(formWithErrors)),
      user => Redirect(routes.Application.main()).withSession("id" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Login.login()).withNewSession.flashing(
      "success" -> "로그아웃 되었습니다."
    )
  }
}

/**
 * Provide security features
 */
trait Secured {
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("id")
  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Login.login)

  // --

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }  

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }


  /**
   * Check if the connected user is a owner of this task.
   */
//  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
//    if(Task.isOwner(task, user)) {
//      f(user)(request)
//    } else {
//      Results.Forbidden
//    }
//  }
}