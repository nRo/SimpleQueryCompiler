// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;


/**
 * Error listener that turns every syntax error into a {@link QueryCompilerException}.
 */
public class QueryCompilerErrorListener extends BaseErrorListener {
    //Query String that lead to an error
    private final String queryString;

    /**
     * Creates a new listener
     *
     * @param queryString query string that is compiled, used in error messages
     */
    public QueryCompilerErrorListener(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Listener method that gets evoked if an error occurs during query compilation.
     * This method throws a {@link QueryCompilerException} if called by the compiler.
     * The message contains the line, the position, the problem and the complete query string.
     *
     * @param recognizer         recognizer
     * @param offendingSymbol    offending symbol
     * @param line               line in input string
     * @param charPositionInLine char position in the input line
     * @param msg                error message
     * @param e                  exception
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new QueryCompilerException(String.format(
                "syntax error at line %d, position %d: %s (query: %s)", line, charPositionInLine, msg, queryString));
    }

    /**
     * Returns the query string that caused an error during compilation
     *
     * @return query string
     */
    public String getQueryString() {
        return queryString;
    }
}
