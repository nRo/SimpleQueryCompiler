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


import de.alexgruen.query.Query;
import de.alexgruen.query.QueryNode;
import de.alexgruen.query.QueryTree;
import de.alexgruen.query.generated.QueryLexer;
import de.alexgruen.query.generated.QueryParser;
import de.alexgruen.query.term.TermOperators;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;

/**
 * Created by Alex on 18.05.2017.
 */
public class QueryTreeCompiler {

    private QueryContext<? extends Query> context;

    public QueryTreeCompiler(QueryContext context) {
        this.context = context;
    }

    /**
     * Compiles an input query string into a {@link QueryTree}.
     * A {@link QueryCompilerException} is thrown if an error occurs.
     * @param queryString input string
     * @return resulting query tree
     */
    public QueryTree compile(String queryString) {
        if (context == null) {
            throw new RuntimeException("context required");
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

        //Don't print errors to stderr
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(errorListener);
        TermQueryVisitor filterPredicateVisitor = new TermQueryVisitor(context);
        return new QueryTree(filterPredicateVisitor.visit(parser.compilationUnit()));
    }

}
