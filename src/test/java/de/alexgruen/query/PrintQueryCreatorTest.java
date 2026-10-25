package de.alexgruen.query;

import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

class PrintQueryCreatorTest {

    private PrintQuery createSimplePrintQuery(final String str) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return str;
            }
        };
    }

    @Test
    void testTermOperations() {
        PrintQueryCreator creator = new PrintQueryCreator();
        QueryNode node = new QueryNode();
        Field field = new Field("x", "x");
        Value value = new Value(123);

        PrintQuery query = creator.eq(node, field, value);
        Assertions.assertEquals("(x == 123)", query.toString());

        query = creator.ne(node, field, value);
        Assertions.assertEquals("(x != 123)", query.toString());

        query = creator.gt(node, field, value);
        Assertions.assertEquals("(x > 123)", query.toString());

        query = creator.ge(node, field, value);
        Assertions.assertEquals("(x >= 123)", query.toString());

        query = creator.lt(node, field, value);
        Assertions.assertEquals("(x < 123)", query.toString());

        query = creator.le(node, field, value);
        Assertions.assertEquals("(x <= 123)", query.toString());

        Value patternValue = new Value(Pattern.compile("test"));
        query = creator.regex(node, field, patternValue);
        Assertions.assertEquals("(x ~= /test/)", query.toString());

        Value textValue = new Value("test");
        query = creator.text(node, field, textValue);
        Assertions.assertEquals("(x *= 'test')", query.toString());

        Value listValue = new Value(List.of(new Value(123), new Value(124)));
        query = creator.in(node, field, listValue);
        Assertions.assertEquals("(x in [123, 124])", query.toString());

        query = creator.notIn(node, field, listValue);
        Assertions.assertEquals("(x !in [123, 124])", query.toString());

        query = creator.fullSearch(textValue);
        Assertions.assertEquals("'test'", query.toString());

        query = creator.empty();
        Assertions.assertEquals("*", query.toString());
    }

    @Test
    void testLogicOperations() {
        PrintQueryCreator creator = new PrintQueryCreator();
        QueryNode node = new QueryNode();

        PrintQuery q1 = createSimplePrintQuery("q1");
        PrintQuery q2 = createSimplePrintQuery("q2");
        PrintQuery q3 = createSimplePrintQuery("q3");

        PrintQuery query = creator.not(node, q1);
        Assertions.assertEquals("!q1", query.toString());

        query = creator.and(node, q1, q2, q3);
        Assertions.assertEquals("(q1 && q2 && q3)", query.toString());

        query = creator.or(node, q1, q2, q3);
        Assertions.assertEquals("(q1 || q2 || q3)", query.toString());

        query = creator.xor(node, q1, q2, q3);
        Assertions.assertEquals("(q1 XOR q2 XOR q3)", query.toString());

        query = creator.nor(node, q1, q2, q3);
        Assertions.assertEquals("(q1 NOR q2 NOR q3)", query.toString());
    }
}
