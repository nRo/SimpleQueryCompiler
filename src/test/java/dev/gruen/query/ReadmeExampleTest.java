package dev.gruen.query;

import dev.gruen.query.compiler.QueryCompiler;
import dev.gruen.query.compiler.QueryCompilerBuilder;
import dev.gruen.query.optimization.RemoveRedundantBrackets;
import dev.gruen.query.term.TermOperators;
import dev.gruen.query.term.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * Keeps the examples from the README compiling and working.
 */
class ReadmeExampleTest {

    /**
     * Target type: a filter that is evaluated against a row of values
     */
    interface RowFilter extends Query {
        boolean test(Map<String, Object> row);
    }

    private static int compare(Object rowValue, Value value) {
        if (rowValue instanceof Number a && value.getValue() instanceof Number b) {
            return Double.compare(a.doubleValue(), b.doubleValue());
        }
        return rowValue.toString().compareTo(String.valueOf(value.getValue()));
    }

    private static QueryCompiler<RowFilter> createCompiler() {
        return QueryCompilerBuilder.create(RowFilter.class)
                .withTermCreator(TermOperators.EQ,
                        (node, field, value) -> row -> row.containsKey(field.toString())
                                && compare(row.get(field.toString()), value) == 0)
                .withTermCreator(TermOperators.GT,
                        (node, field, value) -> row -> row.containsKey(field.toString())
                                && compare(row.get(field.toString()), value) > 0)
                .withANDCreator((node, children) -> row -> Arrays.stream(children).allMatch(c -> c.test(row)))
                .withORCreator((node, children) -> row -> Arrays.stream(children).anyMatch(c -> c.test(row)))
                .withOptimization(new RemoveRedundantBrackets())
                .build();
    }

    @Test
    void testRowFilter() {
        QueryCompiler<RowFilter> compiler = createCompiler();
        RowFilter filter = compiler.compile("age > 18 && (country == 'DE' || country == 'AT')");

        Assertions.assertTrue(filter.test(Map.of("age", 30, "country", "DE")));
        Assertions.assertTrue(filter.test(Map.of("age", 19, "country", "AT")));
        Assertions.assertFalse(filter.test(Map.of("age", 17, "country", "DE")));
        Assertions.assertFalse(filter.test(Map.of("age", 30, "country", "FR")));
    }

    @Test
    void testPrintQuery() {
        QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder
                .createDefault(PrintQuery.class, new PrintQueryCreator())
                .build();

        PrintQuery query = compiler.compile("x > 0 && (y < 1 || z == 'test')");
        Assertions.assertEquals("((x > 0) && ((y < 1) || (z == 'test')))", query.toString());

        QueryTree tree = compiler.compileTree("x > 0 && (y < 1 || z == 'test')");
        String expected = """
                ──┐ AND
                  ├── (x > 0)
                  └──┐ OR
                     ├── (y < 1)
                     └── (z == test)
                """;
        Assertions.assertEquals(expected, QueryPrinter.create().build().toString(tree.getRoot()));
    }
}
