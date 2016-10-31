package com.workshop

import org.specs2.mutable.SpecificationWithJUnit
import scala.concurrent.duration._
import org.specs2.specification.Scope

class RollingWindowThrottlerTest extends SpecificationWithJUnit{
  
  trait ThrottlerContext extends Scope{
    val throttler = new RollingWindowThrottler(max = 1, durationWindow = 1.minute)
    val ip = "192.168.2.1"
  }

  "Rolling window throttler" should {
    "Allow single request" in new ThrottlerContext {
      throttler.tryAcquire(ip) must beTrue
    }
    "Throttle second request" in new ThrottlerContext {
      throttler.tryAcquire(ip) must beTrue
      throttler.tryAcquire(ip) must beFalse
    }  
    "Allow second request but from different key" in new ThrottlerContext {
      val anotherIp = "200.200.200.1"
      throttler.tryAcquire(ip) must beTrue
      throttler.tryAcquire(anotherIp) must beTrue
    }
    
  }

}

class RollingWindowThrottler(max: Int, durationWindow: FiniteDuration) {
  
  val counter = Counter()
  val invocations = scala.collection.mutable.HashMap.empty[String, Counter]
  
  def tryAcquire(key: String): Boolean = {
    val invocationCount = invocations.getOrElseUpdate(key, Counter()).incrementAndGet
    invocationCount <= max
  }
  
}

case class Counter(var count: Int = 0){
  def incrementAndGet: Int = {
    count = count + 1
    count    
  }  
}