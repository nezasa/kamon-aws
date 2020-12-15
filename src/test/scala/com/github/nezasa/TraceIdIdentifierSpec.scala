package com.github.nezasa

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.RawHeader
import com.typesafe.config.ConfigFactory
import kamon.Kamon
import kamon.context.Context
import kamon.trace.{Identifier, Span}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.immutable

class TraceIdIdentifierSpec extends FlatSpec with Matchers {

  val factory = new TraceIdIdentifier.Factory()
  val string = "1-67891233-abcdef012345678912345678"

  "TraceIdIdentifier" should "roundtrip string format correctly" in {
    factory.from(factory.from(string).string).string should be (string)
  }

  it should "roundtrip binary format correctly" in {
    factory.from(factory.from(string).bytes).string should be (string)
  }
}
