package de.alexgruen.query.compiler;

import de.alexgruen.query.PrintQuery;
import de.alexgruen.query.PrintQueryCreator;
import de.alexgruen.query.QueryTree;
import de.alexgruen.query.optimization.Optimizations;
import de.alexgruen.query.term.TermOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompileTest {
    private static final QueryCompiler<PrintQuery> PRINT_COMPILER
            = QueryCompilerBuilder.create(PrintQuery.class)
            .withQueryCreator(new PrintQueryCreator())
            .withOptimization(Optimizations.RemoveRedundantBrackets)
            .build();

    @Test
    void testCompile() {


        test("x > 0", "(x > 0)");
        test("x == 'a'", "(x == 'a')");
        test("x ~= /.+/", "(x ~= /.+/)");
        test("x < \"a\"", "(x < 'a')");
        test("'x' <= \"a\"", "(x <= 'a')");
        test("'x'.y > \"a\"", "(x.y > 'a')");
        test("'x y' <= \"a\"", "('x y' <= 'a')");
        test("x.'x y' *= \"a\"", "(x.'x y' *= 'a')");
        test("x.'x y' == \"a b\"", "(x.'x y' == 'a b')");
        test("x.'x y' == false", "(x.'x y' == false)");
        test("(x > 0) && !(y < 1)", "((x > 0) && !(y < 1))");

        test("x > 0 && y < 1", "((x > 0) && (y < 1))");
        test("x > 0 && y < 1 && z == 2",
                "((x > 0) && (y < 1) && (z == 2))");

        test("(x > 0 && y < 1) || z == 2",
                "(((x > 0) && (y < 1)) || (z == 2))");

        test("(x > 0 && y < 1 || (u == 2 && h != 1)) || z == 2",
                "(((x > 0) && (y < 1)) || ((u == 2) && (h != 1)) || (z == 2))");

        test("(x == 1 || y == 2 || (d == 0 || (t == 3 || z == 1)))",
                ("((x == 1) || (y == 2) || (d == 0) || (t == 3) || (z == 1))"));

    }

    @Test
    void testOptimization() {
        System.out.println("###### remove brackets optimization:");

        QueryCompiler<PrintQuery> noOptimizationCompiler = QueryCompilerBuilder
                .create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .build();
        QueryCompiler<PrintQuery> withOptimizationCompiler = QueryCompilerBuilder
                .create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .withOptimization(Optimizations.RemoveRedundantBrackets)
                .build();


        test("((x > 1) && ((y > 2) && z < 3))",
                "((x > 1) && ((y > 2) && (z < 3)))",
                noOptimizationCompiler, true);

        System.out.println();
        test("((x > 1) && ((y > 2) && z < 3))",
                "((x > 1) && (y > 2) && (z < 3))",
                withOptimizationCompiler, true);
    }

    @Test
    void testException() {
        Assertions.assertThrows(QueryCompilerException.class,
                () -> PRINT_COMPILER.compile("x > a"));

        Assertions.assertThrows(QueryCompilerException.class,
                () -> PRINT_COMPILER.compile("x x > 1"));

        Assertions.assertThrows(QueryCompilerException.class,
                () -> PRINT_COMPILER.compile("(x > 1"));

        Assertions.assertThrows(QueryCompilerException.class,
                () -> PRINT_COMPILER.compile("x &= 1"));
    }

    @Test
    void fullTextSearchTest() {
        test("xyz", "('xyz')");
        test("-xyz", "(!'xyz')");
        test("'xyz'", "('xyz')");
        test("-'xyz'", "(!'xyz')");
        test("'xyz' abc", "('xyz' && 'abc')");
        test("asd xyz -deg 'z e d' -x", "('asd' && 'xyz' && 'z e d' && !'deg' && !'x')");
    }

    @Test
    void testTreeCase1() {
        String str = PRINT_COMPILER.compileTree("(x > 0 && y < 1 || (u == 2 && (h != 1 || z < 2))) || z == 2").toString();
        Assertions.assertEquals("""
                ──┐ OR
                  ├──┐ AND
                  │  ├── (x > 0)
                  │  └── (y < 1)
                  ├──┐ AND
                  │  ├── (u == 2)
                  │  └──┐ OR
                  │     ├── (h != 1)
                  │     └── (z < 2)
                  └── (z == 2)
                """, str);
    }

    @Test
    void testListTerm() {
        test("x IN ('1', '2')", "(x in ['1', '2'])");
        test("x !in ('1', 2, 3.1)", "(x !in ['1', 2, 3.1])");
        test("x in (1,2,3)", "(x in [1, 2, 3])");
        test("x in (1)", "(x in [1])");
        test("x !in (1)", "(x !in [1])");
    }

    @Test
    void testCustomOperator() {
        QueryCompiler<PrintQuery> compiler
                = QueryCompilerBuilder.create(PrintQuery.class)
                .withQueryCreator(new PrintQueryCreator())
                .withTermCreator(
                        new TermOperator("&="),
                        (n, f, v) -> new PrintQuery() {
                            @Override
                            public String toString() {
                                return String.format("(%s &= %s)", f, PrintQueryCreator.value2string(v));
                            }
                        }
                )

                .withOptimization(Optimizations.RemoveRedundantBrackets)
                .build();

        test("x &= 2", "(x &= 2)", compiler, true);
    }

    private void test(String input, String output) {
        test(input, output, PRINT_COMPILER, false);
    }

    private void test(String input, String output, QueryCompiler<PrintQuery> compiler, boolean printTree) {
        QueryTree tree = compiler.compileTree(input);
        if (printTree) {
            System.out.printf("%s ->%n%s%n", input, tree.toString());
        }
        PrintQuery p = compiler.compile(tree);
        Assertions.assertEquals(output, p.toString());
    }
}