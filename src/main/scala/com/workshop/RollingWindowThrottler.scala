package com.workshop

import scala.concurrent.duration.FiniteDuration
import java.time.Clock
import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
import java.util.concurrent.TimeUnit
import com.google.common.base.Ticker

/**
 * Created by kfirb on 10/31/16.
 */
class RollingWindowThrottler(max: Int,
                             durationWindow: FiniteDuration,
                             clock: Clock) {




  val invocations: LoadingCache[String, Counter] = CacheBuilder.newBuilder()
                    .expireAfterWrite(durationWindow.toMillis, TimeUnit.MILLISECONDS)
                    .ticker(throttlingTicker())
                    .build(defaultCounter())

  def tryAcquire(key: String): Boolean =
    invocations.get(key).incrementAndGet <= max


  def defaultCounter(): CacheLoader[String, Counter] = new CacheLoader[String, Counter] {
    override def load(key: String): Counter = Counter()
  }

  def throttlingTicker(): Ticker = new Ticker {
    override def read(): Long = TimeUnit.MILLISECONDS.toNanos(clock.instant().toEpochMilli)
  }

}
