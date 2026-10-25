package de.alexgruen.query.creator;

import de.alexgruen.query.PrintQuery;
import de.alexgruen.query.QueryNode;
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TermCreatorTest {

    @Test
    void testTermCreator() {
        TermCreator<PrintQuery> termCreator = (node, field, value) -> new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s == %s)", field.getFullPath(), value.toString());
            }
        };

        Field field = new Field("x", "x");
        Value value = new Value(123);
        PrintQuery query = termCreator.create(null, field, value);

        Assertions.assertNotNull(query);
        Assertions.assertEquals("(x == 123)", query.toString());

        field = new Field("x.y.z", "x", "y", "z");
        query = termCreator.create(null, field, value);

        Assertions.assertNotNull(query);
        Assertions.assertEquals("(x.y.z == 123)", query.toString());

        value = new Value("test");
        query = termCreator.create(null, field, value);

        Assertions.assertNotNull(query);
        Assertions.assertEquals("(x.y.z == test)", query.toString());

        value = new Value(null);
        query = termCreator.create(null, field, value);

        Assertions.assertNotNull(query);
        Assertions.assertEquals("(x.y.z == null)", query.toString());
    }

    @Test
    void testTermCreatorWithLambda() {
        TermCreator<PrintQuery> termCreator = (node, field, value) -> new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s != %s)", field.getFullPath(), value);
            }
        };

        Field field = new Field("x", "x");
        Value value = new Value(123);
        PrintQuery query = termCreator.create(null, field, value);

        Assertions.assertNotNull(query);
        Assertions.assertEquals("(x != 123)", query.toString());
    }

    @Test
    void testTermCreatorWithQueryNode() {
        TermCreator<PrintQuery> termCreator = (node, field, value) -> new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s %s %s)", field.getFullPath(),
                        node != null && node.isNegate() ? "!=" : "==",
                        value.toString());
            }
        };

        QueryNode node = new QueryNode();
        Field field = new Field("x", "x");
        Value value = new Value(123);
        PrintQuery query = termCreator.create(node, field, value);

        Assertions.assertNotNull(query);
        Assertions.assertEquals("(x == 123)", query.toString());

        node.setNegate(true);
        query = termCreator.create(node, field, value);

        Assertions.assertNotNull(query);
        Assertions.assertEquals("(x != 123)", query.toString());
    }
}