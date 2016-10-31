package com.workshop

import org.specs2.mutable.SpecificationWithJUnit
import scala.concurrent.duration._
import org.specs2.specification.Scope
import com.workshop.framework.FakeClock
import java.time.{Instant, Clock}
import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
import java.util.concurrent.TimeUnit
import com.google.common.base.Ticker

class RollingWindowThrottlerTest extends SpecificationWithJUnit {

  trait ThrottlerContext extends Scope {
    val clock = new FakeClock()
    val throttler = new RollingWindowThrottler(max = 1,
      durationWindow = 1.minute,
      clock = clock)
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
    "Re-allow another request after the rolling window" in new ThrottlerContext {
      throttler.tryAcquire(ip) must beTrue
      throttler.tryAcquire(ip) must beFalse
      clock.age(2.minutes)
      throttler.tryAcquire(ip) must beTrue
    }

  }

}

class RollingWindowThrottler(max: Int,
                             durationWindow: FiniteDuration,
                             clock: Clock) {

  val counter = Counter()

  def defaultCounter(): CacheLoader[String, Counter] = new CacheLoader[String, Counter] {
    override def load(key: String): Counter = Counter()
  }

  def throttlingTicker(): Ticker = new Ticker {
    override def read(): Long = TimeUnit.MILLISECONDS.toNanos(clock.instant().toEpochMilli)
  }

  val invocations: LoadingCache[String, Counter] = CacheBuilder.newBuilder()
                    .expireAfterWrite(durationWindow.toMillis, TimeUnit.MILLISECONDS)
                    .ticker(throttlingTicker())
                    .build(defaultCounter())

  def tryAcquire(key: String): Boolean = {
    invocations.get(key).incrementAndGet <= max
  }

}

case class Counter(var count: Int = 0) {
  def incrementAndGet: Int = {
    count = count + 1
    count
  }
}