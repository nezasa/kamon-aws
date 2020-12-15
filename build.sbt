import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / crossScalaVersions := Seq("2.12.8", "2.13.0")
ThisBuild / organization     := "com.github.nezasa"
ThisBuild / organizationName := "nezasa"
ThisBuild / organizationHomepage := Some(url("https://github.com/nezasa/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/nezasa/kamon-aws/"),
    "scm:git@github.com:nezasa/kamon-aws.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "nezasadev",
    name  = "Nezasa Devs",
    email = "dev@nezasa.com",
    url   = url("http://your.url")
  )
)


ThisBuild / homepage := Some(url("https://github.com/nezasa/kamon-aws"))
ThisBuild / licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

lazy val root = (project in file("."))
  .settings(
    name := "kamon-aws",
    libraryDependencies += kamon,
    libraryDependencies += kamonPlay % Test,
    libraryDependencies += play % Test,
    libraryDependencies += scalaTest % Test,

    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseCrossBuild := true,
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
