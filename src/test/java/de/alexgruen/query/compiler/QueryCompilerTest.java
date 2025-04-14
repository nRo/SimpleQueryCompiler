package de.alexgruen.query.compiler;

import de.alexgruen.query.PrintQuery;
import de.alexgruen.query.PrintQueryCreator;
import de.alexgruen.query.QueryTree;
import de.alexgruen.query.optimization.QueryOptimization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class QueryCompilerTest {

    @Test
    void testCompileWithInvalidInput() {
        QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder.create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .build();

        Assertions.assertThrows(QueryCompilerException.class, () -> compiler.compile("x > "));

        Assertions.assertThrows(QueryCompilerException.class, () -> compiler.compile("(x > 1"));

        Assertions.assertThrows(QueryCompilerException.class, () -> compiler.compile("x @ 1"));
    }

    @Test
    void testCompileTree() {
        QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder.create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .build();

        QueryTree tree = compiler.compileTree("x > 1");
        Assertions.assertNotNull(tree);
        Assertions.assertNotNull(tree.getRoot());

        tree = compiler.compileTree("(x > 1 && y < 2) || z == 3");
        Assertions.assertNotNull(tree);
        Assertions.assertNotNull(tree.getRoot());
        Assertions.assertEquals(2, tree.getRoot().getChildren().size());
    }

    @Test
    void testCompileWithCustomOptimization() {
        final int[] count = {0};
        QueryOptimization countingOptimization = tree -> count[0]++;

        QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder.create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .withOptimization(countingOptimization)
                .build();

        compiler.compileTree("x > 1");
        Assertions.assertEquals(1, count[0]);

        compiler.compileTree("y < 2");
        Assertions.assertEquals(2, count[0]);
    }

    @Test
    void testCompileWithEmptyInput() {
        QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder.create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .build();

        PrintQuery query = compiler.compile("");
        Assertions.assertNotNull(query);

        query = compiler.compile("  ");
        Assertions.assertNotNull(query);
    }

    @Test
    void testCompileWithNoEmptyCreator() {
        QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder.create(PrintQuery.class)
                .withANDCreator((node, children) -> new PrintQuery() {
                    @Override
                    public String toString() {
                        return "and";
                    }
                })
                .build();

        Assertions.assertThrows(QueryCompilerException.class, () -> compiler.compile(""));
    }

    @Test
    void testGetContext() {
        QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder.create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .build();

        Assertions.assertNotNull(compiler.getContext());
        Assertions.assertEquals(PrintQuery.class, compiler.getContext().getCl());
    }

    @Test
    void testGetOptimizations() {
        List<QueryOptimization> optimizations = new ArrayList<>();
        optimizations.add(tree -> {
        });
        optimizations.add(tree -> {
        });

        QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder.create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .withOptimization(optimizations.get(0))
                .withOptimization(optimizations.get(1))
                .build();

        Assertions.assertNotNull(compiler.getOptimizations());
        Assertions.assertEquals(2, compiler.getOptimizations().size());
        Assertions.assertEquals(optimizations.get(0), compiler.getOptimizations().get(0));
        Assertions.assertEquals(optimizations.get(1), compiler.getOptimizations().get(1));
    }
}