package com.airbnbData.model

import org.joda.time.DateTime
import play.api.libs.json.JsValue

/**
  * Implementation independent aggregate root.
  *
  * Note that this uses Joda Time classes and UUID, which are specifically mapped
  * through the custom postgres driver.
  */
case class AirbnbUser(id: Long, firstName: String, about: String, document: JsValue, createdAt: DateTime, updatedAt: Option[DateTime])
