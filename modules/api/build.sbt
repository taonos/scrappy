
scalaVersion := "2.11.8"

lazy val scalazVersion = "7.2.7"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
  "com.typesafe.play" % "play-json_2.11" % "2.5.9",
  "com.vividsolutions" % "jts" % "1.13"
)
