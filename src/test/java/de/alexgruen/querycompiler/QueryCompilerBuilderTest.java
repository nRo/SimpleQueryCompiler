package de.alexgruen.querycompiler;

import de.alexgruen.query.*;
import de.alexgruen.query.compiler.QueryCompiler;
import de.alexgruen.query.compiler.QueryCompilerBuilder;
import de.alexgruen.query.compiler.QueryContext;
import de.alexgruen.query.creator.LogicCreator;
import de.alexgruen.query.creator.TermCreator;
import de.alexgruen.query.optimization.QueryOptimization;
import de.alexgruen.query.optimization.RemoveRedundantBrackets;
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.TermOperator;
import de.alexgruen.query.term.TermOperators;
import de.alexgruen.query.term.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class QueryCompilerBuilderTest {

    @Test
    public void testCreate() {
        QueryCompilerBuilder<PrintQuery> builder = QueryCompilerBuilder.create(PrintQuery.class);
        Assertions.assertNotNull(builder);

        QueryCompiler<PrintQuery> compiler = builder.build();
        Assertions.assertNotNull(compiler);
    }

    @Test
    public void testCreateDefault() {
        PrintQueryCreator creator = new PrintQueryCreator();
        QueryCompilerBuilder<PrintQuery> builder = QueryCompilerBuilder.createDefault(PrintQuery.class, creator);
        Assertions.assertNotNull(builder);

        QueryCompiler<PrintQuery> compiler = builder.build();
        Assertions.assertNotNull(compiler);

        try {
            java.lang.reflect.Field optimizationsField = QueryCompiler.class.getDeclaredField("optimizations");
            optimizationsField.setAccessible(true);
            List<QueryOptimization> optimizations = (List<QueryOptimization>) optimizationsField.get(compiler);

            Assertions.assertEquals(1, optimizations.size());
            Assertions.assertTrue(optimizations.get(0) instanceof RemoveRedundantBrackets);
        } catch (Exception e) {
            Assertions.fail("Failed to access optimizations field: " + e.getMessage());
        }
    }

    @Test
    public void testWithQueryCreator() {
        PrintQueryCreator creator = new PrintQueryCreator();
        QueryCompilerBuilder<PrintQuery> builder = QueryCompilerBuilder.create(PrintQuery.class)
                .withQueryCreator(creator);

        QueryCompiler<PrintQuery> compiler = builder.build();
        try {
            java.lang.reflect.Field contextField = QueryCompiler.class.getDeclaredField("context");
            contextField.setAccessible(true);
            QueryContext<PrintQuery> context = (QueryContext<PrintQuery>) contextField.get(compiler);

            Assertions.assertNotNull(context.getTermCreator(TermOperators.EQ));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.NE));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.LT));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.LE));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.GT));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.GE));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.REGEX));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.TEXT));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.IN));
            Assertions.assertNotNull(context.getTermCreator(TermOperators.FULL_TEXT));

            Assertions.assertNotNull(context.getLogicCreator(LogicalOperators.AND));
            Assertions.assertNotNull(context.getLogicCreator(LogicalOperators.OR));
            Assertions.assertNotNull(context.getLogicCreator(LogicalOperators.XOR));
            Assertions.assertNotNull(context.getLogicCreator(LogicalOperators.NOR));
            Assertions.assertNotNull(context.getLogicCreator(LogicalOperators.NOT));

            Assertions.assertNotNull(context.getEmptyCreator());
        } catch (Exception e) {
            Assertions.fail("Failed to access context field: " + e.getMessage());
        }
    }

    @Test
    public void testWithOptimization() {
        QueryCompilerBuilder<PrintQuery> builder = QueryCompilerBuilder.create(PrintQuery.class)
                .withOptimization(new RemoveRedundantBrackets());

        QueryCompiler<PrintQuery> compiler = builder.build();
        try {
            java.lang.reflect.Field optimizationsField = QueryCompiler.class.getDeclaredField("optimizations");
            optimizationsField.setAccessible(true);
            List<QueryOptimization> optimizations = (List<QueryOptimization>) optimizationsField.get(compiler);

            Assertions.assertEquals(1, optimizations.size());
            Assertions.assertTrue(optimizations.get(0) instanceof RemoveRedundantBrackets);
        } catch (Exception e) {
            Assertions.fail("Failed to access optimizations field: " + e.getMessage());
        }
    }

    @Test
    public void testWithTermCreator() {
        TermCreator<PrintQuery> customCreator = (node, field, value) -> new PrintQuery() {
            @Override
            public String toString() {
                return "custom";
            }
        };

        QueryCompilerBuilder<PrintQuery> builder = QueryCompilerBuilder.create(PrintQuery.class)
                .withTermCreator(new TermOperator("custom"), customCreator);

        QueryCompiler<PrintQuery> compiler = builder.build();
        try {
            java.lang.reflect.Field contextField = QueryCompiler.class.getDeclaredField("context");
            contextField.setAccessible(true);
            QueryContext<PrintQuery> context = (QueryContext<PrintQuery>) contextField.get(compiler);

            Assertions.assertNotNull(context.getTermCreator(new TermOperator("custom")));
        } catch (Exception e) {
            Assertions.fail("Failed to access context field: " + e.getMessage());
        }
    }

    @Test
    public void testWithEmptyCreator() {
        TermCreator<PrintQuery> emptyCreator = (node, field, value) -> new PrintQuery() {
            @Override
            public String toString() {
                return "empty";
            }
        };

        QueryCompilerBuilder<PrintQuery> builder = QueryCompilerBuilder.create(PrintQuery.class)
                .withEmptyCreator(emptyCreator);

        QueryCompiler<PrintQuery> compiler = builder.build();
        try {
            java.lang.reflect.Field contextField = QueryCompiler.class.getDeclaredField("context");
            contextField.setAccessible(true);
            QueryContext<PrintQuery> context = (QueryContext<PrintQuery>) contextField.get(compiler);

            Assertions.assertNotNull(context.getEmptyCreator());
        } catch (Exception e) {
            Assertions.fail("Failed to access context field: " + e.getMessage());
        }
    }

    @Test
    public void testWithLogicCreators() {
        LogicCreator<PrintQuery> andCreator = (node, children) -> new PrintQuery() {
            @Override
            public String toString() {
                return "and";
            }
        };

        LogicCreator<PrintQuery> orCreator = (node, children) -> new PrintQuery() {
            @Override
            public String toString() {
                return "or";
            }
        };

        QueryCompilerBuilder<PrintQuery> builder = QueryCompilerBuilder.create(PrintQuery.class)
                .withANDCreator(andCreator)
                .withORCreator(orCreator);

        QueryCompiler<PrintQuery> compiler = builder.build();
        try {
            java.lang.reflect.Field contextField = QueryCompiler.class.getDeclaredField("context");
            contextField.setAccessible(true);
            QueryContext<PrintQuery> context = (QueryContext<PrintQuery>) contextField.get(compiler);

            Assertions.assertNotNull(context.getLogicCreator(LogicalOperators.AND));
            Assertions.assertNotNull(context.getLogicCreator(LogicalOperators.OR));
        } catch (Exception e) {
            Assertions.fail("Failed to access context field: " + e.getMessage());
        }
    }
}
