package com.workshop

import org.specs2.mutable.SpecificationWithJUnit
import scala.concurrent.duration._

class RollingWindowThrottlerTest extends SpecificationWithJUnit{
  
  "Rolling window throttler" should {
    "Allow single request" in {
      val throttler = new RollingWindowThrottler(max = 1, durationWindow = 1.minute) 
      throttler.tryAcquire("192.168.2.1") must beTrue
    }
    
  }

}

class RollingWindowThrottler(max: Int, durationWindow: FiniteDuration) {
  def tryAcquire(key: String): Boolean = true
  
}