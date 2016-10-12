package com.airbnbData.model.slick.helper

import slick.lifted.MappedTo

/**
  * Created by Lance on 2016-10-12.
  */

final case class PK[A](value: Long) extends AnyVal with MappedTo[Long]
