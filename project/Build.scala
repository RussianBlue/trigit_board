import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "trigit_board"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "com.typesafe.play" %% "play-slick" % "0.4.0"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
}
