package de.alexgruen.query.term;

import de.alexgruen.query.compiler.QueryCompilerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class ValueTest {

    @Test
    @SuppressWarnings("java:S5961")
    void testValueCreation() {
        Value nullValue = new Value(null);
        Assertions.assertTrue(nullValue.isNull());
        Assertions.assertEquals(Value.Type.NULL, nullValue.getType());
        Assertions.assertNull(nullValue.getValue());
        Assertions.assertNull(nullValue.getString());

        Double doubleVal = 123.45;
        Value doubleValue = new Value(doubleVal);
        Assertions.assertEquals(Value.Type.DOUBLE, doubleValue.getType());
        Assertions.assertEquals(doubleVal, doubleValue.getValue());
        Assertions.assertTrue(doubleValue.isNumber());
        Assertions.assertEquals(doubleVal, doubleValue.getDouble());
        Assertions.assertEquals(123L, doubleValue.getLong());

        Long longVal = 123L;
        Value longValue = new Value(longVal);
        Assertions.assertEquals(Value.Type.LONG, longValue.getType());
        Assertions.assertEquals(longVal, longValue.getValue());
        Assertions.assertTrue(longValue.isNumber());
        Assertions.assertEquals(123.0, longValue.getDouble());
        Assertions.assertEquals(longVal, longValue.getLong());

        String stringVal = "test";
        Value stringValue = new Value(stringVal);
        Assertions.assertEquals(Value.Type.STRING, stringValue.getType());
        Assertions.assertEquals(stringVal, stringValue.getValue());
        Assertions.assertTrue(stringValue.isString());
        Assertions.assertEquals(stringVal, stringValue.getString());

        Boolean boolVal = true;
        Value boolValue = new Value(boolVal);
        Assertions.assertEquals(Value.Type.BOOLEAN, boolValue.getType());
        Assertions.assertEquals(boolVal, boolValue.getValue());
        Assertions.assertTrue(boolValue.isBoolean());
        Assertions.assertEquals(boolVal, boolValue.getBoolean());

        Pattern patternVal = Pattern.compile("test");
        Value patternValue = new Value(patternVal);
        Assertions.assertEquals(Value.Type.PATTERN, patternValue.getType());
        Assertions.assertEquals(patternVal, patternValue.getValue());
        Assertions.assertTrue(patternValue.isPattern());
        Assertions.assertEquals(patternVal, patternValue.getPattern());

        Integer intVal = 123;
        Value intValue = new Value(intVal);
        Assertions.assertEquals(Value.Type.LONG, intValue.getType());
        Assertions.assertEquals(123L, intValue.getValue());

        Float floatVal = 123.45f;
        Value floatValue = new Value(floatVal);
        Assertions.assertEquals(Value.Type.DOUBLE, floatValue.getType());
        Assertions.assertEquals(123.45, floatValue.getDouble(), 0.001);
    }

    @Test
    void testListValue() {
        List<Value> valueList = new ArrayList<>();
        valueList.add(new Value("test1"));
        valueList.add(new Value(123L));

        Value listValue = new Value(valueList);
        Assertions.assertEquals(Value.Type.LIST, listValue.getType());
        Assertions.assertTrue(listValue.isList());
        Assertions.assertEquals(valueList, listValue.getList());
        Assertions.assertEquals(2, listValue.getList().size());
        Assertions.assertEquals("test1", listValue.getList().get(0).getString());
        Assertions.assertEquals(123L, listValue.getList().get(1).getLong());
    }

    @Test
    void testInvalidListValue() {
        List<String> invalidList = new ArrayList<>();
        invalidList.add("test");

        Assertions.assertThrows(QueryCompilerException.class, () -> new Value(invalidList));
    }

    @Test
    void testTypeConversionExceptions() {
        Value stringValue = new Value("test");

        Assertions.assertThrows(QueryCompilerException.class, stringValue::getDouble);

        Assertions.assertThrows(QueryCompilerException.class, stringValue::getLong);

        Assertions.assertThrows(QueryCompilerException.class, stringValue::getBoolean);

        Assertions.assertThrows(QueryCompilerException.class, stringValue::getPattern);

        Assertions.assertThrows(QueryCompilerException.class, stringValue::getList);
    }

    @Test
    void testUnsupportedType() {
        File input = new File("test");
        Assertions.assertThrows(QueryCompilerException.class, () -> new Value(input));
    }

    @Test
    void testToString() {
        Value stringValue = new Value("test");
        Assertions.assertEquals("test", stringValue.toString());

        Value nullValue = new Value(null);
        Assertions.assertNull(nullValue.toString());
    }
}