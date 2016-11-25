
scalaVersion := "2.11.8"

lazy val scalazVersion = "7.2.7"
lazy val playVersion = "2.5.10"
lazy val slickVersion = "3.2.0-M1"
lazy val circeVersion = "0.5.1"
lazy val http4sVersion = "0.14.11a"

libraryDependencies ++=
  Seq(
    "io.circe" %% "circe-core"
  ).map(_ % circeVersion) ++
  Seq(
    "org.scalaz" %% "scalaz-core",
    "org.scalaz" %% "scalaz-concurrent"
  ).map(_ % scalazVersion) ++
  Seq(
    "com.typesafe.play" %% "play-ws"
  ).map(_ % playVersion) ++
  Seq(
    "org.http4s" %% "http4s-blaze-client"
  ).map(_ % http4sVersion) ++
  Seq(
    "com.typesafe.slick" %% "slick"
  ).map(_ % slickVersion) ++
  Seq(
    "com.vividsolutions" % "jts" % "1.13"
  )
