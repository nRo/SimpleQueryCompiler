package de.alexgruen.querycompiler;

import de.alexgruen.query.compiler.QueryCompilerException;
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Value;
import de.alexgruen.query.util.CompilerUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompilerUtilTest {

    @Test
    public void testCreateArray() {
        String[] stringArray = CompilerUtil.createArray(String.class, 3);
        Assertions.assertNotNull(stringArray);
        Assertions.assertEquals(3, stringArray.length);

        Integer[] intArray = CompilerUtil.createArray(Integer.class, 5);
        Assertions.assertNotNull(intArray);
        Assertions.assertEquals(5, intArray.length);

        Value[] valueArray = CompilerUtil.createArray(Value.class, 2);
        Assertions.assertNotNull(valueArray);
        Assertions.assertEquals(2, valueArray.length);

        Object[] emptyArray = CompilerUtil.createArray(Object.class, 0);
        Assertions.assertNotNull(emptyArray);
        Assertions.assertEquals(0, emptyArray.length);
    }

    @Test
    public void testCreateField() {
        Field simpleField = new Field("x", "x");
        Assertions.assertEquals("x", simpleField.getFullPath());
        Assertions.assertEquals(1, simpleField.getPath().length);
        Assertions.assertEquals("x", simpleField.getPath()[0]);

        Field nestedField = new Field("x.y.z", "x", "y", "z");
        Assertions.assertEquals("x.y.z", nestedField.getFullPath());
        Assertions.assertEquals(3, nestedField.getPath().length);
        Assertions.assertEquals("x", nestedField.getPath()[0]);
        Assertions.assertEquals("y", nestedField.getPath()[1]);
        Assertions.assertEquals("z", nestedField.getPath()[2]);
    }

    @Test
    public void testCreateValue() {
        Value nullValue = new Value(null);
        Assertions.assertTrue(nullValue.isNull());

        Value stringValue = new Value("test");
        Assertions.assertEquals("test", stringValue.getString());

        Value numberValue = new Value(123.45);
        Assertions.assertEquals(123.45, numberValue.getDouble());

        Value boolValue = new Value(true);
        Assertions.assertTrue(boolValue.getBoolean());
    }
}
