package com.airbnbData.slick.dao.helper

/**
  * Created by Lance on 1/29/17.
  */

/**
  * Entity which is identified by a key.
  * @tparam ID The key
  */
trait Keyed[ID] {
  def id: slick.lifted.Rep[ID]
}
