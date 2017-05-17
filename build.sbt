import com.typesafe.config.ConfigFactory
import scala.util.{Try, Success, Failure}

val btVersion: String = {
  Try(ConfigFactory.load.getString("version")) match {
    case Success(ver) => ver
    case Failure(_) => "0.1.0"
  }
}

name := "reactive-mongo"
version := btVersion
scalaVersion := "2.11.11"
organization := "com.cjww-dev.libs"

val cjwwDep: Seq[ModuleID] = Seq(
  "com.cjww-dev.libs" % "logging_2.11" % "0.5.0",
  "com.cjww-dev.libs" % "bootstrapper_2.11" % "1.4.2"
)

val codeDep: Seq[ModuleID] = Seq(
  "com.typesafe.play" % "play_2.11" % "2.5.14",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14"
)

libraryDependencies ++= cjwwDep
libraryDependencies ++= codeDep

bintrayOrganization := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := true
bintrayRepository := "releases"
bintrayOmitLicense := true
