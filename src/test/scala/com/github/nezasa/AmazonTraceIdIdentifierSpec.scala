package com.github.nezasa

import org.scalatest.{FlatSpec, Matchers}

class AmazonTraceIdIdentifierSpec extends FlatSpec with Matchers {

  val factory = new AmazonTraceIdIdentifier.Factory()
  val string = "1-67891233-abcdef012345678912345678"

  "TraceIdIdentifier" should "roundtrip string format correctly" in {
    factory.from(factory.from(string).string).string should be (string)
  }

  it should "roundtrip binary format correctly" in {
    factory.from(factory.from(string).bytes).string should be (string)
  }

  it should "roundtrip generated identifier" in {
    val generated = factory.generate()
    factory.from(generated.string) should be (generated)
  }
}
