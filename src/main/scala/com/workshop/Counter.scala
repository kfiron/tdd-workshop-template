package com.workshop

/**
 * Created by kfirb on 10/31/16.
 */
case class Counter(var count: Int = 0) {
  def incrementAndGet: Int = {
    count = count + 1
    count
  }
}
