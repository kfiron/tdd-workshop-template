package com.workshop

import org.specs2.mutable.SpecificationWithJUnit
import scala.concurrent.duration._

class RollingWindowThrottlerTest extends SpecificationWithJUnit{
  
  "Rolling window throttler" should {
    "Allow single request" in {
      val throttler = new RollingWindowThrottler(max = 1, durationWindow = 1.minute) 
      throttler.tryAcquire("192.168.2.1") must beTrue
    }
    "Throttle second request" in {
      val throttler = new RollingWindowThrottler(max = 1, durationWindow = 1.minute)
      throttler.tryAcquire("192.168.2.1") must beTrue
      throttler.tryAcquire("192.168.2.1") must beFalse
    }
    
  }

}

class RollingWindowThrottler(max: Int, durationWindow: FiniteDuration) {
  
  val counter = Counter()
  
  def tryAcquire(key: String): Boolean = {
    val invocationCount = counter.incrementAndGet
    invocationCount <= max
  }
  
}

case class Counter(var count: Int = 0){
  def incrementAndGet: Int = {
    count = count + 1
    count    
  }  
}