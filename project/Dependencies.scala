import sbt._

object Dependencies {
  val kamon = "io.kamon" %% "kamon-core" % "2.0.0"
  val kamonPlay =  "io.kamon" %% "kamon-play" % "2.0.0"
  val play =  "com.typesafe.play" %% "play-akka-http-server" % "2.7.3"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
}
