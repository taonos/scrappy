package com.airbnbData.model

import java.util.UUID

import org.joda.time.{DateTime, Instant}



/**
 * Implementation independent aggregate root.
 *
 * Note that this uses Joda Time classes and UUID, which are specifically mapped
 * through the custom postgres driver.
 */
case class User(id: UUID, email: String, createdAt: DateTime, updatedAt: Option[DateTime])

