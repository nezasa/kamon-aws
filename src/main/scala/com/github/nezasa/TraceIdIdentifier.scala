package com.github.nezasa

import java.nio.ByteBuffer
import java.util.UUID
import kamon.trace.Identifier
import kamon.util.HexCodec

import java.util.function.Supplier
import scala.util.Try
import java.util.concurrent.TimeUnit.MILLISECONDS

class TraceIdIdentifier extends kamon.trace.Identifier.Scheme(
  traceIdFactory = new TraceIdIdentifier.Factory(),
  spanIdFactory = new TraceIdIdentifier.Factory()
)

object TraceIdIdentifier {
  class Factory extends kamon.trace.Identifier.Factory {
    override def generate(): Identifier = {
      val startTime: Long = MILLISECONDS.toSeconds(System.currentTimeMillis)
      val number: Long = 1
      traceIDToIdentifier(new TraceID(HexCodec.toLowerHex(startTime),HexCodec.toLowerHex(number)))
    }

    override def from(string: String): Identifier = TraceID.fromString(string)
      .map[Identifier](new java.util.function.Function[TraceID, Identifier]() {
        override def apply(t: TraceID): Identifier = {
          traceIDToIdentifier(t)
        }
      })
      .orElseGet(() => Identifier.Empty)

    override def from(bytes: Array[Byte]): Identifier = {
      val buffer = ByteBuffer.wrap(bytes)
      val traceID = new TraceID(HexCodec.toLowerHex(buffer.getInt()).substring(8), HexCodec.toLowerHex(buffer.getInt()).substring(8) + HexCodec.toLowerHex(buffer.getLong()))
      traceIDToIdentifier(traceID)
    }

    private def traceIDToIdentifier(t: TraceID) = {
      val data = ByteBuffer.allocate(20)
      data.putInt(t.getStartTime)
      t.getNumberAsHex.grouped(8).map(java.lang.Long.parseLong(_, 16).toInt).foreach(data.putInt(_))
      new Identifier(t.toString, data.array())
    }
  }
}

