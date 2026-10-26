package dev.gruen.query;

import dev.gruen.query.compiler.QueryCompiler;
import dev.gruen.query.compiler.QueryCompilerBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the logical operators of the grammar: XOR support, operator precedence and associativity.
 * Precedence from highest to lowest: AND, XOR, OR/NOR. All binary operators are left-associative.
 */
class PrecedenceTest {

    private final QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder
            .createDefault(PrintQuery.class, new PrintQueryCreator())
            .build();

    private String compile(String query) {
        return compiler.compile(query).toString();
    }

    @Test
    void testXor() {
        Assertions.assertEquals("((a == 1) XOR (b == 2))", compile("a == 1 xor b == 2"));
        Assertions.assertEquals("((a == 1) XOR (b == 2))", compile("a == 1 XOR b == 2"));
    }

    @Test
    void testNor() {
        Assertions.assertEquals("((a == 1) NOR (b == 2))", compile("a == 1 nor b == 2"));
    }

    @Test
    void testAndBindsTighterThanOr() {
        Assertions.assertEquals("((a == 1) || ((b == 2) && (c == 3)))", compile("a == 1 || b == 2 && c == 3"));
        Assertions.assertEquals("(((a == 1) && (b == 2)) || (c == 3))", compile("a == 1 && b == 2 || c == 3"));
    }

    @Test
    void testXorBetweenAndAndOr() {
        Assertions.assertEquals("((a == 1) || ((b == 2) XOR (c == 3)))", compile("a == 1 || b == 2 xor c == 3"));
        Assertions.assertEquals("(((a == 1) && (b == 2)) XOR (c == 3))", compile("a == 1 && b == 2 xor c == 3"));
    }

    @Test
    void testSameOperatorChainIsFlattened() {
        Assertions.assertEquals("((a == 1) || (b == 2) || (c == 3))", compile("a == 1 || b == 2 || c == 3"));
    }

    @Test
    void testLeftAssociative() {
        Assertions.assertEquals("(((a == 1) XOR (b == 2)) NOR (c == 3))", compile("a == 1 xor b == 2 nor c == 3"));
    }

    @Test
    void testBracketsOverridePrecedence() {
        Assertions.assertEquals("(((a == 1) || (b == 2)) && (c == 3))", compile("(a == 1 || b == 2) && c == 3"));
    }

    @Test
    void testNegatedGroups() {
        String negated = compile("a == 1 && !(b == 2 || c == 3)");
        Assertions.assertNotEquals(compile("a == 1 && (b == 2 || c == 3)"), negated);
        Assertions.assertTrue(negated.contains("!"));
    }

    @Test
    void testDoubleNegationCancelsOut() {
        Assertions.assertEquals(compile("(a == 1 || b == 2)"), compile("!(!(a == 1 || b == 2))"));
    }
}
