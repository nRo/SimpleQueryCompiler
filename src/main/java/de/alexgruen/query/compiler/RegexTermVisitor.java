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

import de.alexgruen.query.QueryNode;
import de.alexgruen.query.generated.QueryBaseVisitor;
import de.alexgruen.query.generated.QueryParser;
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Term;
import de.alexgruen.query.term.TermOperators;
import de.alexgruen.query.term.Value;
import de.alexgruen.query.util.CompilerUtil;

import java.util.regex.Pattern;

/**
 * Created by Alex on 21.05.2017.
 */
public class RegexTermVisitor extends QueryBaseVisitor<QueryNode> {

    /**
     * Creates a {@link QueryNode} that represents a regex term.
     * An exception is thrown if the context can not be converted to a node
     * @param ctx input regex context
     * @return query node
     */
    @Override
    public QueryNode visitRegex_term(QueryParser.Regex_termContext ctx) {
        Field field = CompilerUtil.createField(ctx.variable());

        Pattern pattern = convertPattern(ctx.REGEX().getText());
        Value value = new Value(pattern);
        return new QueryNode(new Term(field,TermOperators.REGEX, value));
    }

    /**
     * Converts an input string to a {@link Pattern}.
     * A {@link QueryCompilerException} is thrown if the input string is not in the right format
     * @param text input string
     * @return compiled pattern
     */
    private static Pattern convertPattern(String text){
        String regex = text;
        if(!regex.startsWith("/") || ! regex.endsWith("/")){
            throw new QueryCompilerException(String.format("wrong pattern format: %s",text));
        }
        regex = regex.substring(1,regex.length()-1);
        return Pattern.compile(regex);

    }



}
