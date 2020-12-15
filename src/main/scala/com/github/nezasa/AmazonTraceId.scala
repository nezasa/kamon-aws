package com.github.nezasa

import kamon.Kamon
import kamon.context.HttpPropagation.HeaderReader
import kamon.context.{Context, Propagation}
import kamon.trace.{Identifier, Span, Trace}

/**
 * Reads the traceId from a configurable header name
 */
class AmazonTraceId extends Propagation.EntryReader[HeaderReader] {
  override def read(reader: HeaderReader, context: Context): Context = {
    val identityProvider = Kamon.identifierScheme

    val headerName = "X-Amzn-Trace-Id"

    val traceHeader = reader.read(headerName)
      .map(id => TraceHeader.fromString(id))

    traceHeader match {
      case Some(traceHeader) =>
        val traceId = Option(traceHeader.getRootTraceId).map(identityProvider.traceIdFactory.from).getOrElse(Identifier.Empty)
        val parentId = Option(traceHeader.getParentId).map(identityProvider.spanIdFactory.from).getOrElse(Identifier.Empty)
        val samplingDecision = TraceHeader.samplingDecisionFromString(traceHeader.getSampled)
        context.withEntry(Span.Key, Span.Remote(id = Identifier.Empty, parentId = parentId, trace = Trace(traceId, samplingDecision)))
      case None => context
    }

  }
}
