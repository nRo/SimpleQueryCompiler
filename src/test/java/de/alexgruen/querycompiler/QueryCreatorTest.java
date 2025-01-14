package de.alexgruen.querycompiler;

import de.alexgruen.query.*;
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

public class QueryCreatorTest {

    private static class TestQueryCreator implements QueryCreator<PrintQuery> {
        @Override
        public PrintQuery ne(QueryNode queryNode, Field field, Value value) {
            return createPrintQuery(String.format("(%s != %s)", field.getFullPath(), value));
        }

        @Override
        public PrintQuery eq(QueryNode queryNode, Field field, Value value) {
            return createPrintQuery(String.format("(%s == %s)", field.getFullPath(), value));
        }

        @Override
        public PrintQuery ge(QueryNode queryNode, Field field, Value value) {
            return createPrintQuery(String.format("(%s >= %s)", field.getFullPath(), value));
        }

        @Override
        public PrintQuery gt(QueryNode queryNode, Field field, Value value) {
            return createPrintQuery(String.format("(%s > %s)", field.getFullPath(), value));
        }

        @Override
        public PrintQuery lt(QueryNode queryNode, Field field, Value value) {
            return createPrintQuery(String.format("(%s < %s)", field.getFullPath(), value));
        }

        @Override
        public PrintQuery le(QueryNode queryNode, Field field, Value value) {
            return createPrintQuery(String.format("(%s <= %s)", field.getFullPath(), value));
        }

        @Override
        public PrintQuery regex(QueryNode queryNode, Field field, Value value) {
            return createPrintQuery(String.format("(%s ~= %s)", field.getFullPath(), value));
        }

        @Override
        public PrintQuery text(QueryNode queryNode, Field field, Value value) {
            return createPrintQuery(String.format("(%s *= %s)", field.getFullPath(), value));
        }

        @Override
        public PrintQuery in(QueryNode queryNode, Field field, Value list) {
            return createPrintQuery(String.format("(%s in %s)", field.getFullPath(), list));
        }

        @Override
        public PrintQuery notIn(QueryNode queryNode, Field field, Value list) {
            return createPrintQuery(String.format("(%s !in %s)", field.getFullPath(), list));
        }

        @Override
        public PrintQuery not(QueryNode queryNode, PrintQuery v) {
            return createPrintQuery(String.format("!%s", v));
        }

        @Override
        public PrintQuery and(QueryNode queryNode, PrintQuery[] v) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < v.length; i++) {
                sb.append(v[i]);
                if (i < v.length - 1) {
                    sb.append(" && ");
                }
            }
            sb.append(")");
            return createPrintQuery(sb.toString());
        }

        @Override
        public PrintQuery or(QueryNode queryNode, PrintQuery[] v) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < v.length; i++) {
                sb.append(v[i]);
                if (i < v.length - 1) {
                    sb.append(" || ");
                }
            }
            sb.append(")");
            return createPrintQuery(sb.toString());
        }

        @Override
        public PrintQuery xor(QueryNode queryNode, PrintQuery[] v) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < v.length; i++) {
                sb.append(v[i]);
                if (i < v.length - 1) {
                    sb.append(" ^ ");
                }
            }
            sb.append(")");
            return createPrintQuery(sb.toString());
        }

        @Override
        public PrintQuery nor(QueryNode queryNode, PrintQuery[] v) {
            StringBuilder sb = new StringBuilder();
            sb.append("!(");
            for (int i = 0; i < v.length; i++) {
                sb.append(v[i]);
                if (i < v.length - 1) {
                    sb.append(" || ");
                }
            }
            sb.append(")");
            return createPrintQuery(sb.toString());
        }

        @Override
        public PrintQuery fullSearch(Value value) {
            return createPrintQuery(String.format("'%s'", value));
        }

        @Override
        public PrintQuery empty() {
            return createPrintQuery("*");
        }

        private PrintQuery createPrintQuery(final String str) {
            return new PrintQuery() {
                @Override
                public String toString() {
                    return str;
                }
            };
        }
    }

    @Test
    public void testTermOperations() {
        TestQueryCreator creator = new TestQueryCreator();
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
        Assertions.assertEquals("(x ~= test)", query.toString());

        Value textValue = new Value("test");
        query = creator.text(node, field, textValue);
        Assertions.assertEquals("(x *= test)", query.toString());

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
    public void testLogicOperations() {
        TestQueryCreator creator = new TestQueryCreator();
        QueryNode node = new QueryNode();

        PrintQuery q1 = creator.createPrintQuery("q1");
        PrintQuery q2 = creator.createPrintQuery("q2");
        PrintQuery q3 = creator.createPrintQuery("q3");

        PrintQuery query = creator.not(node, q1);
        Assertions.assertEquals("!q1", query.toString());

        query = creator.and(node, new PrintQuery[]{q1, q2, q3});
        Assertions.assertEquals("(q1 && q2 && q3)", query.toString());

        query = creator.or(node, new PrintQuery[]{q1, q2, q3});
        Assertions.assertEquals("(q1 || q2 || q3)", query.toString());

        query = creator.xor(node, new PrintQuery[]{q1, q2, q3});
        Assertions.assertEquals("(q1 ^ q2 ^ q3)", query.toString());

        query = creator.nor(node, new PrintQuery[]{q1, q2, q3});
        Assertions.assertEquals("!(q1 || q2 || q3)", query.toString());
    }
}
