
// Adding this means no explicit import in *.scala.html files
//TwirlKeys.templateImports += "com.airbnbData.dao.User"

lazy val http4sVersion = "0.14.11a"

scalaVersion := "2.11.8"
routesGenerator := InjectedRoutesGenerator
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)