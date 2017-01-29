
// Adding this means no explicit import in *.scala.html files
//TwirlKeys.templateImports += "com.airbnbData.dao.User"

lazy val enumeratumVersion = "1.5.6"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++=
  Seq(
    "com.beachape" %% "enumeratum-play"
  ).map(_ % enumeratumVersion)