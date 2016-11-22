
scalaVersion := "2.11.8"

lazy val scalazVersion = "7.2.7"
lazy val playVersion = "2.5.10"
lazy val slickVersion = "3.1.1"

libraryDependencies ++=
  Seq(
    "org.scalaz" %% "scalaz-core",
    "org.scalaz" %% "scalaz-concurrent"
  ).map(_ % scalazVersion) ++
  Seq(
    "com.typesafe.play" % "play-json_2.11",
    "com.typesafe.play" %% "play-ws"
  ).map(_ % playVersion) ++
  Seq(
    "com.typesafe.slick" %% "slick"
  ).map(_ % slickVersion) ++
  Seq(
    "com.vividsolutions" % "jts" % "1.13"
  )
