package de.alexgruen.querycompiler;

import de.alexgruen.query.compiler.QueryCompilerException;
import de.alexgruen.query.compiler.RegexTermVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class RegexTermVisitorTest {

    @Test
    public void testConvertPattern() throws Exception {
        Method convertPatternMethod = RegexTermVisitor.class.getDeclaredMethod("convertPattern", String.class);
        convertPatternMethod.setAccessible(true);

        Pattern pattern = (Pattern) convertPatternMethod.invoke(null, "/test/");
        Assertions.assertNotNull(pattern);
        Assertions.assertTrue(pattern.matcher("test").matches());
        Assertions.assertFalse(pattern.matcher("other").matches());

        pattern = (Pattern) convertPatternMethod.invoke(null, "/t.st/");
        Assertions.assertNotNull(pattern);
        Assertions.assertTrue(pattern.matcher("test").matches());
        Assertions.assertTrue(pattern.matcher("tast").matches());
        Assertions.assertFalse(pattern.matcher("tst").matches());

        pattern = (Pattern) convertPatternMethod.invoke(null, "/\\d+/");
        Assertions.assertNotNull(pattern);
        Assertions.assertTrue(pattern.matcher("123").matches());
        Assertions.assertFalse(pattern.matcher("abc").matches());
    }

    @Test
    public void testConvertPatternInvalid() throws Exception {
        Method convertPatternMethod = RegexTermVisitor.class.getDeclaredMethod("convertPattern", String.class);
        convertPatternMethod.setAccessible(true);

        try {
            convertPatternMethod.invoke(null, "test/");
            Assertions.fail("Expected QueryCompilerException was not thrown");
        } catch (Exception e) {
            Assertions.assertInstanceOf(QueryCompilerException.class, e.getCause());
            Assertions.assertTrue(e.getCause().getMessage().contains("wrong pattern format"));
        }

        try {
            convertPatternMethod.invoke(null, "/test");
            Assertions.fail("Expected QueryCompilerException was not thrown");
        } catch (Exception e) {
            Assertions.assertInstanceOf(QueryCompilerException.class, e.getCause());
            Assertions.assertTrue(e.getCause().getMessage().contains("wrong pattern format"));
        }

        try {
            convertPatternMethod.invoke(null, "test");
            Assertions.fail("Expected QueryCompilerException was not thrown");
        } catch (Exception e) {
            Assertions.assertInstanceOf(QueryCompilerException.class, e.getCause());
            Assertions.assertTrue(e.getCause().getMessage().contains("wrong pattern format"));
        }
    }
}
