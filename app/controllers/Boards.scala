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
  def getCategory_id(category_type: Any): Long =
    category_type match {
      case "notice" => 1
      case "report" => 2
      case "storyboard" => 3
      case "other" => 4
      case _ => 0
  }
  //날짜 바꾸기
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  //각 해당하는 리스트 보기
  def list(category:String, page:Long) = IsAuthenticated { username => _ =>
    User.findByEmail(username).map {
      user =>
        val board_id = getCategory_id(category)
        val pageLength = 10
        val boards = Board.list(board_id, page - 1, pageLength)

        val count:Long = Board.getCountCategory(board_id)
        Ok(views.html.main(views.html.project.project(), views.html.frame.board(boards, user, category, count, page, pageLength)))
    }.getOrElse(Forbidden)
  }

  //신규보드 생성 페이지로 이동
  def newBoard(category:String) = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      Ok(views.html.main(views.html.project.project(), views.html.form.write(writeForm, user, category, "게시글 작성")))
    }.getOrElse(Forbidden)
  }

  //기존보드 수정하기 페이지로 이동
  def editBoard(id:Long, category:String) = IsAuthenticated { username => _ =>
    Board.findById(id).map { _board =>
      Ok(views.html.main(views.html.project.project(), views.html.form.edit(writeForm, _board, category, "게시글 수정하기")))
    }.getOrElse(Forbidden)
  }

  //게시판 읽기 페이지로 이동
  def readBoard(id:Long, category:String) = IsAuthenticated { username => _ =>
    Board.findById(id).map { _board =>
      Board.increaseReading(id, _board.readings)
      Ok(views.html.main(views.html.project.project(), views.html.form.read(_board, category)))
    }.getOrElse(Forbidden)
  }

  def create(category:String) = Action(parse.multipartFormData) { implicit request => 
    writeForm.bindFromRequest.fold(
      formWithErrors => BadRequest,
      {
        case (title, contnet) =>
          //session get email   
          Logger.info("Trying to upload a file")

          User.findByEmail(request.session("email")).map {
            user =>
            //이메일
            val email = user.email
            //이름
            val name = user.name
            //카테고리
            val category_id = getCategory_id(category)
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
            request.body.file("clip_file_name").map {
              clip_file =>              
                val fileName = clip_file.filename
                val fileType = clip_file.contentType
                val fileSize = 1
                //파일 업로드 시간
                var fileDate = Some(date(curTimeFormat.format(today)))

                clip_file.ref.moveTo(new File("/tmp/"))

                val boards =  Board.create(
                  Board(NotAssigned, email, name, category_id, title, contnet, readings, createDate, updateDate, fileName, fileType, fileSize, fileDate)
                )              
            }.getOrElse{
              val fileName = ""
              val contentType = ""
              val fileSize = 0
              //파일 업로드 시간
              var fileDate = Some(date(curTimeFormat.format(today)))
              val boards =  Board.create(
                Board(NotAssigned, email, name, category_id, title, contnet, readings, createDate, updateDate, fileName, Some(contentType), fileSize, fileDate)
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