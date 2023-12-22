ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Cockroach",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.16"

  )


