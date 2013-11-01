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
object Boards extends Controller with Secured {
  /*
    board_category_id
    notice : 1
    report : 2
    storyboard : 3
    other : 4
   */
  //Form
  val writeForm = Form(
    tuple(
      "title" -> text,
      "content" -> text
    )
  )
  //보드타입 체크
  def getProject_id(project_type: Any): Long =
    project_type match {
      case "notice" => 1
      case "report" => 2
      case "storyboard" => 3
      case "other" => 4
      case _ => 0
  }
  //날짜 파싱
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  //각 해당하는 리스트 보기
  def list(category:String, page:Long) = IsAuthenticated { user => _ =>
    User.findById(user).map 
    {
      user =>
        val board_id = getProject_id(category)
        val pageLength = 10
        val boards = Board.list(board_id, page - 1, pageLength)
        val count:Long = Board.getCountCategory(board_id)
        Ok
        //Ok(views.html.main(views.html.project.project(), views.html.frame.board(boards, user, category, count, page, pageLength)))
    }.getOrElse(Forbidden)
  }

  //신규보드 생성 페이지로 이동
  def newBoard(project_id:String) = IsAuthenticated { user => _ =>
    User.findById(user).map { user =>
      //Ok(views.html.main(views.html.project.project(), views.html.form.write(writeForm, user, project_id, category, "게시글 작성")))
      Ok
    }.getOrElse(Forbidden)
  }

  //기존보드 수정하기 페이지로 이동
  def editBoard(project_id:Long, category:String) = IsAuthenticated { user => _ =>
    Board.findByProject(project_id).map { 
      _board =>
      // Ok(views.html.main(views.html.project.project(), views.html.form.edit(writeForm, _board, category, "게시글 수정하기")))
      Ok
    }.getOrElse(Forbidden)
  }

  //게시판 읽기 페이지로 이동
  def readBoard(project_id:Long, category:String) = IsAuthenticated { user => _ =>
    Board.findByProject(project_id).map { 
      _board =>
      Board.increaseReading(project_id, _board.readings)
      // Ok(views.html.main(views.html.project.project(), views.html.form.read(_board, category)))
      Ok
    }.getOrElse(Forbidden)
  }

  def replyBoard(project_id:Long, category:String) = IsAuthenticated { user => _ =>
      Board.findByProject(project_id).map { 
      _board =>
      //리딩수 올리기
      Board.increaseReading(project_id, _board.readings)
      // Ok(views.html.main(views.html.project.project(), views.html.form.read(_board, category)))
      Ok
    }.getOrElse(Forbidden)
  }

  def commentBoard(project_id:Long, category:String) = IsAuthenticated { user => _ =>
      Board.findByProject(project_id).map { _board =>
      Board.increaseReading(project_id, _board.readings)
      // Ok(views.html.main(views.html.project.project(), views.html.form.read(_board, category)))
      Ok
    }.getOrElse(Forbidden)
  }

  def create(category:String) = Action(parse.multipartFormData) { implicit request => 
    writeForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      {
        case (title, contnet) =>
          //session get email   
          User.findById(request.session("user_id")).map {
            user =>
            //이메일
            val user_id = user.user_id
            //이름
            val project_id = 1
            //카테고리
            val category_id = getProject_id(category)
            //리딩수
            val readings = 0
            //현재 시간
            val today = Calendar.getInstance.getTime
            //시간타입변환
            val curTimeFormat = new SimpleDateFormat("yyyy-MM-dd")
            //현재시간 
            val createDate = Some(date(curTimeFormat.format(today)))
            //업데이트 시간
            val updateDate = Some(date(curTimeFormat.format(today)))
            //첨부파일이 있는 경우
            val parent_id = project_id

            request.body.file("clip_file_name").map {
              clip_file =>              
                val fileName = clip_file.filename
                val fileType = clip_file.contentType
                Logger.info("Trying to upload a file")                
                val fileSize = 1
                //파일 업로드 시간
                var fileDate = Some(date(curTimeFormat.format(today)))
                clip_file.ref.moveTo(new File("/tmp/picture"))
                
                val boards =  Board.create(
                  Board(NotAssigned, user_id, project_id, category_id, title, contnet, readings, createDate, updateDate, fileName, fileType, fileSize, fileDate, parent_id)
                )              
            }.getOrElse{
              val fileName = ""
              val contentType = ""
              val fileSize = 0
              //파일 업로드 시간
              var fileDate = Some(date(curTimeFormat.format(today)))
              val boards =  Board.create(
                Board(NotAssigned, user_id, project_id, category_id, title, contnet, readings, createDate, updateDate, fileName, Some(contentType), fileSize, fileDate, parent_id)
              )
            } 
          }                   
          Redirect(routes.Boards.list(category, 1))
      }
    )
  }

  def update (id:Long) = IsAuthenticated { username => _ =>
    Board.update(id)
    Ok
  }

  def remove (id:Long, category:String) = IsAuthenticated { username => _ =>
    Board.delete(id)
    Ok
  }
}