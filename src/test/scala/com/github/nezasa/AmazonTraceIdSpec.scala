package com.github.nezasa

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.RawHeader
import com.typesafe.config.ConfigFactory
import kamon.Kamon
import kamon.context.Context
import kamon.trace.{Identifier, Span}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.immutable

class AmazonTraceIdSpec extends FlatSpec with Matchers {
  Kamon.reconfigure(ConfigFactory.parseString("""kamon.trace.identifier-scheme: "com.github.nezasa.AmazonTraceIdIdentifier" """).withFallback(ConfigFactory.load()))
  
  "RequestIdAsTraceId" should "read without headers" in {
    val httpRequest = HttpRequest()
    val context = new AmazonTraceId().read(kamon.instrumentation.akka.http.AkkaHttpInstrumentation.toRequest(httpRequest), Context.Empty)
    
    context.get(Span.Key).trace.id should be (Identifier.Empty)
  }
  
  it should "read with headers" in {
    val httpRequest = HttpRequest(headers = immutable.Seq(RawHeader("X-Amzn-Trace-Id","Root=1-67891233-abcdef012345678912345678")))
    val context = new AmazonTraceId().read(kamon.instrumentation.akka.http.AkkaHttpInstrumentation.toRequest(httpRequest), Context.Empty)

    context.get(Span.Key).trace.id.string should be ("1-67891233-abcdef012345678912345678")
  }
}
