import slick.codegen.SourceCodeGenerator
import slick.{ model => m }

lazy val slickPgVersion = "0.15.0-M2"
lazy val slickVersion = "3.2.0-M1"
lazy val scalazVersion = "7.2.7"
lazy val playVersion = "2.5.10"
lazy val circeVersion = "0.5.1"
lazy val http4sVersion = "0.14.11a"
lazy val monixVersion = "2.1.1"

libraryDependencies ++=
  Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-optics"
  ).map(_ % circeVersion) ++
  Seq(
    "com.typesafe.slick" %% "slick",
    "com.typesafe.slick" %% "slick-hikaricp"
  ).map(_ % slickVersion) ++
  Seq(
    "com.github.tminglei" %% "slick-pg",
    "com.github.tminglei" %% "slick-pg_play-json",
    "com.github.tminglei" %% "slick-pg_joda-time",
    "com.github.tminglei" %% "slick-pg_jts",
    "com.github.tminglei" %% "slick-pg_circe-json"
  ).map(_ % slickPgVersion) ++
  Seq(
    "com.typesafe.play" %% "play-ws"
  ).map(_ % playVersion) ++
  Seq(
    "org.scalaz" %% "scalaz-core"
  ).map(_ % scalazVersion) ++
  Seq(
    "io.monix" %% "monix",
    "io.monix" %% "monix-scalaz-72"
  ).map(_ % monixVersion) ++
  Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-client"
  ).map(_ % http4sVersion) ++
  Seq(
    "com.zaxxer" % "HikariCP" % "2.5.1",
    "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0"
  )


lazy val databaseUrl = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:postgresql:test_db")
lazy val databaseUser = sys.env.getOrElse("DB_DEFAULT_USER", "scrapper")
lazy val databasePassword = sys.env.getOrElse("DB_DEFAULT_PASSWORD", "12345678")

slickCodegenSettings
slickCodegenDatabaseUrl := databaseUrl
slickCodegenDatabaseUser := databaseUser
slickCodegenDatabasePassword := databasePassword
slickCodegenDriver := slick.driver.PostgresDriver
slickCodegenJdbcDriver := "org.postgresql.Driver"
slickCodegenOutputPackage := "com.airbnbData.slick.dao"
slickCodegenExcludedTables := Seq("schema_version")

slickCodegenCodeGenerator := { (model:  m.Model) =>
  new SourceCodeGenerator(model) {
    import slick.profile.SqlProfile.ColumnOption

    override def code =
      "import org.joda.time.DateTime\n" + super.code
    override def Table = new Table(_) {
      override def Column = new Column(_) {
        override def rawType = model.tpe match {
          case "java.sql.Timestamp" => "DateTime" // kill j.s.Timestamp
          // currently, all types that's not built-in support were mapped to `String`
          case "String" => model.options.find(_.isInstanceOf[ColumnOption.SqlType])
            .map(_.asInstanceOf[ColumnOption.SqlType].typeName).map({
            case "hstore" => "Map[String, String]"
            case "geometry" => "com.vividsolutions.jts.geom.Geometry"
            case "int8[]" => "List[Long]"
            case _ =>  "String"
          }).getOrElse("String")
          case _ => super.rawType
        }
      }
    }

    // ensure to use our customized postgres driver at `import profile.simple._`
    override def packageCode(profile: String, pkg: String, container: String, parentType: Option[String]) : String = {
      s"""
package ${pkg}
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object ${container} extends {
  val profile = MyPostgresDriver
} with ${container}
/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait ${container}${parentType.map(t => s" extends $t").getOrElse("")} {
  val profile: MyPostgresDriver
  import profile.api._
  ${indent(code)}
}
      """.trim()
    }
  }
}

//lazy val slick = TaskKey[Seq[File]]("gen-tables")

// code generation task
//sourceGenerators in Compile <+= slickCodegen
