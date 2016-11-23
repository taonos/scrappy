package com.airbnbData.util.FutureOps

import scala.language.implicitConversions
import scala.concurrent.Future

/**
  * Created by Lance on 2016-11-22.
  */
object Implicits {
  implicit def futureToOps[B](future: Future[B]): FutureOps[B] = new FutureOps(future)
}
