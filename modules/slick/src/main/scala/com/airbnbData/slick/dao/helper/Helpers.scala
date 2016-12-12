package com.airbnbData.slick.dao.helper

import slick.lifted.MappedTo

/**
  * Created by Lance on 2016-10-12.
  */

case class PK[A](value: Long) extends AnyVal with MappedTo[Long]
