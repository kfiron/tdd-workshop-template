package com.workshop

import org.specs2.mutable.SpecWithJUnit

class SampleTest extends SpecWithJUnit{

  "foo" should {
    "bar" in {
      4 must beEqualTo(4)
    }
  }

}
