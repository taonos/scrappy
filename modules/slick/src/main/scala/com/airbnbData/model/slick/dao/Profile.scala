package com.airbnbData.model.slick.dao

import com.airbnbData.model.slick.MyPostgresDriver

/**
  * Created by Lance on 2016-10-12.
  */


trait Profile {
  val profile: MyPostgresDriver
}
