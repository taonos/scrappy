package com.airbnbData.util.TaskOps

import scala.language.implicitConversions
import scalaz.concurrent.Task

/**
  * Created by Lance on 2016-11-22.
  */
object Implicits {
  implicit def taskToOps[B](task: Task[B]): TaskOps[B] = new TaskOps[B](task)
}
