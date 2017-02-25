import com.typesafe.config.ConfigFactory
import scala.util.{Try, Success, Failure}

val btVersion: String = {
  Try(ConfigFactory.load.getString("version")) match {
    case Success(ver) => ver
    case Failure(_) => "INVALID_RELEASE_VERSION"
  }
}

name := "reactive-mongo"
version := btVersion
scalaVersion := "2.11.8"
organization := "com.cjww-dev.libs"

val cjwwDep: Seq[ModuleID] = Seq("com.cjww-dev.libs" % "logging_2.11" % "0.1.0")

val codeDep: Seq[ModuleID] = Seq(
  "com.typesafe.play" % "play_2.11" % "2.5.12",
  "org.reactivemongo" %% "reactivemongo" % "0.11.14",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14"
)
val testDep: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "1.5.1",
  "org.mockito" % "mockito-core" % "1.8.5"
)

libraryDependencies ++= cjwwDep
libraryDependencies ++= codeDep
libraryDependencies ++= testDep

bintrayOrganization := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := false
bintrayRepository := "releases"
bintrayOmitLicense := true
