/*
 * MIT License
 *
 * Copyright (c) 2018 Alexander Grün
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.alexgruen.query.compiler;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;


/**
 * Created by Alex on 21.05.2017.
 */
public class QueryCompilerErrorListener extends BaseErrorListener {
    //Query String that lead to an error
    private String queryString;

    public QueryCompilerErrorListener(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Listener method that gets evoked if an error occurs during query compilation.
     * This method throws {@link QueryCompilerException} if called by the compiler
     * @param recognizer recognizer
     * @param offendingSymbol offending symbol
     * @param line line in input string
     * @param charPositionInLine char position in input line
     * @param msg error message
     * @param e exception
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new QueryCompilerException(String.format("syntax error (%s : %s) line:%s, pos:%s ", msg, queryString, line, charPositionInLine));
    }

    /**
     * Returns the query string that caused an error during compilation
     * @return
     */
    public String getQueryString() {
        return queryString;
    }
}
