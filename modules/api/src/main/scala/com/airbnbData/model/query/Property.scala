package com.airbnbData.model.query

/**
  * Created by Lance on 12/13/16.
  */

case class Property(detail: PropertyDetail, user: AirbnbUser)
  extends AggregateRoot
