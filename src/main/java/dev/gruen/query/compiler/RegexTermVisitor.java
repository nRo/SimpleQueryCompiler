// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;

import dev.gruen.query.QueryNode;
import dev.gruen.query.generated.QueryBaseVisitor;
import dev.gruen.query.generated.QueryParser;
import dev.gruen.query.term.Field;
import dev.gruen.query.term.Term;
import dev.gruen.query.term.TermOperators;
import dev.gruen.query.term.Value;
import dev.gruen.query.util.CompilerUtil;

import java.util.regex.Pattern;

public class RegexTermVisitor extends QueryBaseVisitor<QueryNode> {

    /**
     * Creates a {@link QueryNode} that represents a regex term.
     * An exception is thrown if the context cannot be converted to a node
     *
     * @param ctx input regex context
     * @return query node
     */
    @Override
    public QueryNode visitRegex_term(QueryParser.Regex_termContext ctx) {
        Field field = CompilerUtil.createField(ctx.variable());

        Pattern pattern = convertPattern(ctx.REGEX().getText());
        Value value = new Value(pattern);
        return new QueryNode(new Term(field, TermOperators.REGEX, value));
    }

    /**
     * Converts an input string to a {@link Pattern}.
     * A {@link QueryCompilerException} is thrown if the input string is not in the right format
     *
     * @param text input string
     * @return compiled pattern
     */
    private static Pattern convertPattern(String text) {
        String regex = text;
        if (!regex.startsWith("/") || !regex.endsWith("/")) {
            throw new QueryCompilerException(String.format("wrong pattern format: %s", text));
        }
        regex = regex.substring(1, regex.length() - 1);
        return Pattern.compile(regex);
    }
}
