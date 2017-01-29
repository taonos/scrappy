package com.airbnbData.service.interpreter

import com.airbnbData.model.query.Property
import com.airbnbData.service.PropertyService
import monix.reactive.Consumer

import scalaz.Kleisli

/**
  * Created by Lance on 12/13/16.
  */
class PropertyServiceInterpreter extends PropertyService[Property] {
  override def all(all: () => ObservableOp[Property]): TaskOp[Seq[Property]] =
    all().mapT { obv =>
      obv
        .consumeWith(Consumer.foldLeft[List[Property], Property](List()) { case (acc, i) => i :: acc })
        .map(_.reverse)
    }

}
