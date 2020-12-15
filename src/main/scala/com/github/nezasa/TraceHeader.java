package com.github.nezasa;

import kamon.trace.*;

public class TraceHeader {
    private static final String DELIMITER = ";";

    private static final String ROOT_PREFIX = "Root=";
    private static final String PARENT_PREFIX = "Parent=";
    private static final String SAMPLED_PREFIX = "Sampled=";
    private static final String SELF_PREFIX = "Self=";

//    public enum SampleDecision {
//        SAMPLED("Sampled=1"), NOT_SAMPLED("Sampled=0"), UNKNOWN(""), REQUESTED("Sampled=?");
//
//        private final String value;
//
//        SampleDecision(String value) {
//            this.value = value;
//        }
//
//        @Override
//        public String toString() {
//            return value;
//        }
//
//        public static SampleDecision fromString(String text) {
//            for (SampleDecision decision : SampleDecision.values()) {
//                if (decision.toString().equalsIgnoreCase(text)) {
//                    return decision;
//                }
//            }
//            return UNKNOWN;
//        }
//    }

    public static Trace.SamplingDecision samplingDecisionFromString(String text) {
        switch (text) {
            case "Sampled=1":
                return Trace$SamplingDecision$Sample$.MODULE$; //Trace.SamplingDecision.Sample$.MODULE$;
            case "Sampled=0":
                return Trace$SamplingDecision$DoNotSample$.MODULE$; //Trace.SamplingDecision.DoNotSample$.MODULE$;
            default:
                return Trace$SamplingDecision$Unknown$.MODULE$; //Trace.SamplingDecision.Unknown$.MODULE$;
        }
    }

    private String rootTraceId;
    private String parentId;
    private String sampled;

    public TraceHeader() {
        this(null, null, "");
    }

    public TraceHeader(String rootTraceId, String parentId, String sampled) {
        this.rootTraceId = rootTraceId;
        this.parentId = parentId;
        this.sampled = sampled;
    }

    /**
     * Creates a TraceHeader object from a String. Note that this will silently ignore any "Self=" trace ids injected from ALB.
     *
     * @param string
     *            the string from an incoming trace-id header
     * @return the TraceHeader object
     */
    public static TraceHeader fromString(String string) {
        TraceHeader traceHeader = new TraceHeader();

        if (string == null) {
            return traceHeader;
        }

        int pos = 0;
        while (pos < string.length()) {
            int delimiterIndex = string.indexOf(';', pos);
            final String part;
            if (delimiterIndex >= 0) {
                part = string.substring(pos, delimiterIndex);
                pos = delimiterIndex + 1;
            } else {
                // Last part.
                part = string.substring(pos);
                pos = string.length();
            }
            String trimmedPart = part.trim();
            int equalsIndex = trimmedPart.indexOf('=');
            if (equalsIndex < 0) {
                continue;
            }

            String value = trimmedPart.substring(equalsIndex + 1);

            if (trimmedPart.startsWith(ROOT_PREFIX)) {
                traceHeader.setRootTraceId(value);
            } else if (trimmedPart.startsWith(PARENT_PREFIX)) {
                traceHeader.setParentId(value);
            } else if (trimmedPart.startsWith(SAMPLED_PREFIX)) {
                traceHeader.setSampled(trimmedPart);
            } /*else if (!trimmedPart.startsWith(SELF_PREFIX)) {
                String key = trimmedPart.substring(0, equalsIndex);
                traceHeader.putAdditionalParam(key, value);
            }*/
        }
        return traceHeader;
    }

    /**
     * Serializes the TraceHeader object into a String.
     *
     * @return the String representation of this TraceHeader
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (rootTraceId != null) {
            buffer.append(ROOT_PREFIX).append(rootTraceId).append(DELIMITER);
        }
        if (parentId != null && !parentId.isBlank()) {
            buffer.append(PARENT_PREFIX).append(parentId).append(DELIMITER);
        }
        buffer.append(sampled).append(DELIMITER);
        buffer.setLength(buffer.length() - DELIMITER.length());
        return buffer.toString();
    }

    /**
     * @return the rootTraceId
     */
    public String getRootTraceId() {
        return rootTraceId;
    }

    /**
     * @param rootTraceId the rootTraceId to set
     */
    public void setRootTraceId(String rootTraceId) {
        this.rootTraceId = rootTraceId;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the sampled
     */
    public String getSampled() {
        return sampled;
    }

    /**
     * Sets the sample decision.
     * @param sampled
     *            the non-null SampleDecision to set
     */
    public void setSampled(String sampled) {
        this.sampled = sampled;
    }
}
