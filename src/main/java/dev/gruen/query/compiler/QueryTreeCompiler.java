// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;


import dev.gruen.query.Query;
import dev.gruen.query.QueryNode;
import dev.gruen.query.QueryTree;
import dev.gruen.query.generated.QueryLexer;
import dev.gruen.query.generated.QueryParser;
import dev.gruen.query.term.TermOperators;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;

public class QueryTreeCompiler<T extends Query> {

    private final QueryContext<T> context;

    public QueryTreeCompiler(QueryContext<T> context) {
        this.context = context;
    }

    /**
     * Compiles an input query string into a {@link QueryTree}.
     * A {@link QueryCompilerException} is thrown if an error occurs.
     *
     * @param queryString input string
     * @return resulting query tree
     */
    public QueryTree compile(String queryString) {
        if (context == null) {
            throw new QueryCompilerException("context required");
        }

        queryString = queryString.trim();
        //return "empty" query tree if input string is empty
        if (queryString.isEmpty()) {
            return new QueryTree(new QueryNode());
        }
        QueryCompilerErrorListener errorListener = new QueryCompilerErrorListener(queryString);
        CharStream stream = CharStreams.fromString(queryString);
        QueryLexer lexer = new QueryLexer(stream);

        //Don't print errors to stderr
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QueryParser parser = new QueryParser(tokens);

        //Add all non-default operator names to the parser.
        for (String a : context.getAllTermOperatorAliases()) {
            if (!TermOperators.isDefaultAlias(a)) {
                parser.getCustomOperators().add(a);
            }
        }

        parser.setColumnValues(context.isColumnValues());

        //Don't print errors to stderr
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(errorListener);
        TermQueryVisitor filterPredicateVisitor = new TermQueryVisitor(context);
        return new QueryTree(filterPredicateVisitor.visit(parser.compilationUnit()));
    }

}
