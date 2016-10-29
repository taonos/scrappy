package com.airbnbData.slick.dao.helper

import java.net.URL

import com.github.tminglei.slickpg._
import play.api.libs.json.{JsValue, Json}

/**
 * A postgresql driver with extended Joda and JSON support.
 */
trait MyPostgresDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDateSupportJoda
  with PgRangeSupport
  with PgHStoreSupport
  with PgPlayJsonSupport
  with PgSearchSupport
  with PgPostGISSupport
  with PgNetSupport
  with PgLTreeSupport {

  object MyAPI extends API
    with DateTimeImplicits
    with JsonImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with PostGISImplicits
    with PostGISAssistants
    with SearchImplicits
    with SearchAssistants {

    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

    implicit val playJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse)(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)

    // Mapping between String and URL
    implicit val strURLTypeMapper = MappedColumnType.base[URL, String](
      url => url.toString,
      str => new URL(str)
    )
  }

  // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"
  def pgjson = "jsonb"

  override val api = MyAPI
}

object MyPostgresDriver extends MyPostgresDriver