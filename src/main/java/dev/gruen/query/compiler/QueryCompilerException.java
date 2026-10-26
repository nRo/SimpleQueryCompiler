// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;

/**
 * Exception that is thrown if an error occurs during query compilation
 */
public class QueryCompilerException extends RuntimeException {
    public QueryCompilerException(String message) {
        super(message);
    }

    public QueryCompilerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
