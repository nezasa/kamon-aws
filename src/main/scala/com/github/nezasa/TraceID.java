/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.github.nezasa;


import java.math.BigInteger;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class TraceID {

    /**
     * Returns the {@link TraceID} parsed out of the {@link String}. If the parse fails, a new {@link TraceID} will be returned,
     * effectively restarting the trace.
     */
    public static Optional<TraceID> fromString(String xrayTraceId) {
        xrayTraceId = xrayTraceId.trim();

        if (xrayTraceId.length() != TRACE_ID_LENGTH) {
            return Optional.empty();
        }

        // Check version trace id version
        if (xrayTraceId.charAt(0) != VERSION) {
            return Optional.empty();
        }

        // Check delimiters
        if (xrayTraceId.charAt(TRACE_ID_DELIMITER_INDEX_1) != DELIMITER
            || xrayTraceId.charAt(TRACE_ID_DELIMITER_INDEX_2) != DELIMITER) {
            return Optional.empty();
        }

        String startTimePart = xrayTraceId.substring(TRACE_ID_DELIMITER_INDEX_1 + 1, TRACE_ID_DELIMITER_INDEX_2);
        if (!isHex(startTimePart)) {
            return Optional.empty();
        }
        String randomPart = xrayTraceId.substring(TRACE_ID_DELIMITER_INDEX_2 + 1, TRACE_ID_LENGTH);
        if (!isHex(randomPart)) {
            return Optional.empty();
        }

        return Optional.of(new TraceID(startTimePart, randomPart));
    }

    private static final int TRACE_ID_LENGTH = 35;
    private static final int TRACE_ID_DELIMITER_INDEX_1 = 1;
    private static final int TRACE_ID_DELIMITER_INDEX_2 = 10;

    private static final char VERSION = '1';
    private static final char DELIMITER = '-';

    private String numberHex;
    private String startTimeHex;

    TraceID(String startTimeHex, String numberHex) {
        this.startTimeHex = startTimeHex;
        this.numberHex = numberHex;
    }

    @Override
    public String toString() {
        return "" + VERSION + DELIMITER + startTimeHex + DELIMITER + numberHex;
    }

    public BigInteger getNumber() {
        return new BigInteger(numberHex, 16);
    }

    /**
     * Returns the number component of this {@link TraceID} as a hexadecimal string.
     */
    public String getNumberAsHex() {
        return numberHex;
    }

    public Integer getStartTime() {
        return Integer.valueOf(startTimeHex, 16);
    }

    /**
     * Returns the start time of this {@link TraceID} as a hexadecimal string representing the number of seconds since
     * the epoch.
     */
    public String getStartTimeAsHex() {
        return startTimeHex;
    }

    @Override
    public int hashCode() {
        return 31 * numberHex.hashCode() + startTimeHex.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TraceID)) {
            return false;
        }
        TraceID other = (TraceID) obj;
        return numberHex.equals(other.numberHex) && startTimeHex.equals(other.startTimeHex);
    }

    // Visible for testing
    static boolean isHex(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!isDigit(c) && !isLowercaseHexCharacter(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLowercaseHexCharacter(char b) {
        return 'a' <= b && b <= 'f';
    }

    private static boolean isDigit(char b) {
        return '0' <= b && b <= '9';
    }
}
