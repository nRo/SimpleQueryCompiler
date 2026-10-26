package dev.gruen.query.compiler;

import dev.gruen.query.PrintQuery;
import dev.gruen.query.PrintQueryCreator;
import dev.gruen.query.QueryNode;
import dev.gruen.query.QueryTree;
import dev.gruen.query.optimization.Optimizations;
import dev.gruen.query.optimization.QueryOptimization;
import dev.gruen.query.term.Field;
import dev.gruen.query.term.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

class CodeQualityTest {

    private final QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder
            .createDefault(PrintQuery.class, new PrintQueryCreator())
            .build();

    @Test
    void testNullLiteralIsPassedAsNullValue() {
        AtomicReference<Value> received = new AtomicReference<>();
        QueryCompiler<PrintQuery> eqCompiler = QueryCompilerBuilder
                .createDefault(PrintQuery.class, new PrintQueryCreator() {
                    @Override
                    public PrintQuery eq(QueryNode node, Field field, Value value) {
                        received.set(value);
                        return super.eq(node, field, value);
                    }
                })
                .build();

        eqCompiler.compile("x == null");

        Assertions.assertNotNull(received.get());
        Assertions.assertTrue(received.get().isNull());
        Assertions.assertEquals(Value.Type.NULL, received.get().getType());
    }

    @Test
    void testNullValuesInListAndFullText() {
        Assertions.assertEquals("(x in [null, 1])", compiler.compile("x in (null, 1)").toString());
        Assertions.assertDoesNotThrow(() -> compiler.compile("null"));
    }

    @Test
    void testSyntaxErrorMessageContainsPositionAndQuery() {
        QueryCompilerException e = Assertions.assertThrows(QueryCompilerException.class,
                () -> compiler.compile("x == 1 &&& y"));
        Assertions.assertTrue(e.getMessage().startsWith("syntax error at line 1, position "), e.getMessage());
        Assertions.assertTrue(e.getMessage().contains("(query: x == 1 &&& y)"), e.getMessage());
    }

    @Test
    void testUnknownCustomOperatorFails() {
        QueryCompilerException e = Assertions.assertThrows(QueryCompilerException.class,
                () -> compiler.compile("x &= 1"));
        Assertions.assertTrue(e.getMessage().contains("x &= 1"), e.getMessage());
    }

    @Test
    void testNegatedEmptyNodeIsNegated() {
        QueryNode empty = new QueryNode();
        empty.setNegate(true);
        Assertions.assertEquals("!*", compiler.compile(new QueryTree(empty)).toString());
    }

    @Test
    void testOptimizationsAreCopiedAndUnmodifiable() {
        QueryCompilerBuilder<PrintQuery> builder = QueryCompilerBuilder
                .createDefault(PrintQuery.class, new PrintQueryCreator());
        QueryCompiler<PrintQuery> built = builder.build();
        int size = built.getOptimizations().size();

        builder.withOptimization(Optimizations.RemoveRedundantBrackets);

        Assertions.assertEquals(size, built.getOptimizations().size());
        List<QueryOptimization> optimizations = built.getOptimizations();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> optimizations.add(tree -> { }));
    }
}
