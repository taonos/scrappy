package com.airbnbData.service

import scala.concurrent.{ExecutionContext, Future}

import scalaz._

import com.airbnbData.repository.PropertyRepository

/**
  * Created by Lance on 2016-10-26.
  */
trait PropertyService[Property, AirbnbUser] {
//  type Valid[A] = NonEmptyList[String] \/ A
  type Valid[A] = EitherT[Future, NonEmptyList[String], A]
  type PropertyOperation[A] = Kleisli[Valid, PropertyRepository, A]

  def find(id: String): PropertyOperation[Property]
}
