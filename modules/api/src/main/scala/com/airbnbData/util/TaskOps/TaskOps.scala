package com.airbnbData.util.TaskOps

import scala.concurrent.{Future, Promise}
import scalaz.concurrent.Task

/**
  * Created by Lance on 2016-11-22.
  */

final class TaskOps[A](x: => Task[A]) {
  import scalaz.{-\/, \/-}

  private val p: Promise[A] = Promise()
  def runFuture: Future[A] = {
    x.unsafePerformAsync {
      case -\/(ex) =>
        p.failure(ex); ()
      case \/-(r) => p.success(r); ()
    }
    p.future
  }
}
