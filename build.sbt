import com.typesafe.config.ConfigFactory
import scala.util.{Try, Success, Failure}

val libraryName = "reactive-mongo"

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

val dependencies: Seq[ModuleID] = Seq(
  "com.cjww-dev.libs"      % "application-utilities_2.11" % "2.8.0",
  "com.typesafe.play"      % "play_2.11"                  % "2.5.16",
  "org.reactivemongo"     %% "play2-reactivemongo"        % "0.11.14",
  "org.scalatestplus.play" % "scalatestplus-play_2.11"    % "2.0.1",
  "org.mockito"            % "mockito-core"               % "2.10.0"
)

lazy val library = Project(libraryName, file("."))
  .settings(
    version                               :=  btVersion,
    scalaVersion                          :=  "2.11.12",
    organization                          :=  "com.cjww-dev.libs",
    libraryDependencies                   ++= dependencies,
    bintrayOrganization                   :=  Some("cjww-development"),
    bintrayReleaseOnPublish in ThisBuild  :=  true,
    bintrayRepository                     :=  "releases",
    bintrayOmitLicense                    :=  true
  )
