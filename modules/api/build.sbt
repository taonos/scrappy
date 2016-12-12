
lazy val scalazVersion = "7.2.8"
lazy val playVersion = "2.5.10"
lazy val slickVersion = "3.2.0-M2"
lazy val circeVersion = "0.6.1"
lazy val http4sVersion = "0.15.0a"
lazy val monixVersion = "2.1.1"
lazy val shapelessVersion = "2.3.2"
lazy val enumeratumVersion = "1.5.2"

libraryDependencies ++=
  Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-optics"
  ).map(_ % circeVersion) ++
  Seq(
    "com.chuusai" %% "shapeless"
  ).map(_ % shapelessVersion) ++
  Seq(
    "org.scalaz" %% "scalaz-core"
  ).map(_ % scalazVersion) ++
  Seq(
    "io.monix" %% "monix",
    "io.monix" %% "monix-scalaz-72"
  ).map(_ % monixVersion) ++
  Seq(
    "com.typesafe.play" %% "play-ws"
  ).map(_ % playVersion) ++
  Seq(
    "com.beachape" %% "enumeratum",
    "com.beachape" %% "enumeratum-circe"
  ).map(_ % enumeratumVersion) ++
  Seq(
    "org.http4s" %% "http4s-blaze-client"
  ).map(_ % http4sVersion) ++
  Seq(
    "com.typesafe.slick" %% "slick"
  ).map(_ % slickVersion) ++
  Seq(
    "com.vividsolutions" % "jts" % "1.13"
  )
