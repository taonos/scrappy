package com.airbnbData.util.FutureOps

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaz.concurrent.Task

final class FutureOps[A](x: => Future[A]) {
  import scalaz.Scalaz._

  def asTask: Task[A] = {
    Task.async {
      register =>
        x.onComplete {
          case Success(v) => register(v.right)
          case Failure(ex) => register(ex.left)
        }(play.api.libs.iteratee.Execution.Implicits.trampoline)
    }
  }
}
