import com.typesafe.config.ConfigFactory
import scala.util.{Try, Success, Failure}

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

name         := "reactive-mongo"
version      := btVersion
scalaVersion := "2.11.11"
organization := "com.cjww-dev.libs"

val cjwwDep: Seq[ModuleID] = Seq("com.cjww-dev.libs" % "application-utilities_2.11" % "2.0.1")

val codeDep: Seq[ModuleID] = Seq(
  "com.typesafe.play"  % "play_2.11"           % "2.5.16",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14"
)

val testDep: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" % "scalatestplus-play_2.11" % "2.0.1",
  "org.mockito"            % "mockito-core"            % "2.8.47"
)

libraryDependencies ++= cjwwDep
libraryDependencies ++= codeDep
libraryDependencies ++= testDep

bintrayOrganization                   := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild  := true
bintrayRepository                     := "releases"
bintrayOmitLicense                    := true
