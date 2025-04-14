package de.alexgruen.query.compiler.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

class ParserUtilTest {

    @Test
    void testParse() throws ParseException {
        String stringResult = ParserUtil.parse(String.class, "test");
        Assertions.assertEquals("test", stringResult);
        
        Integer intResult = ParserUtil.parse(Integer.class, "123");
        Assertions.assertEquals(123, intResult);
        
        Double doubleResult = ParserUtil.parse(Double.class, "123.45");
        Assertions.assertEquals(123.45, doubleResult);
        
        Boolean boolResult = ParserUtil.parse(Boolean.class, "true");
        Assertions.assertTrue(boolResult);
        
        Boolean boolResultShort = ParserUtil.parse(Boolean.class, "t");
        Assertions.assertTrue(boolResultShort);
        
        Boolean boolResultFalse = ParserUtil.parse(Boolean.class, "false");
        Assertions.assertFalse(boolResultFalse);
        
        Boolean boolResultFalseShort = ParserUtil.parse(Boolean.class, "f");
        Assertions.assertFalse(boolResultFalseShort);
        
        Assertions.assertThrows(ParseException.class, () -> ParserUtil.parse(Boolean.class, "invalid"));
    }
    
    @Test
    void testParseArray() throws ParseException {
        String[] stringArray = ParserUtil.parse(String[].class, "a,b,c");
        Assertions.assertEquals(3, stringArray.length);
        Assertions.assertEquals("a", stringArray[0]);
        Assertions.assertEquals("b", stringArray[1]);
        Assertions.assertEquals("c", stringArray[2]);
        
        Integer[] intArray = ParserUtil.parse(Integer[].class, "1;2;3");
        Assertions.assertEquals(3, intArray.length);
        Assertions.assertEquals(1, intArray[0]);
        Assertions.assertEquals(2, intArray[1]);
        Assertions.assertEquals(3, intArray[2]);
        
        Double[] doubleArray = ParserUtil.parse(Double[].class, "1.1|2.2|3.3");
        Assertions.assertEquals(3, doubleArray.length);
        Assertions.assertEquals(1.1, doubleArray[0]);
        Assertions.assertEquals(2.2, doubleArray[1]);
        Assertions.assertEquals(3.3, doubleArray[2]);
    }
    
    @Test
    void testHasParser() {
        Assertions.assertTrue(ParserUtil.hasParser(String.class));
        Assertions.assertTrue(ParserUtil.hasParser(Integer.class));
        Assertions.assertTrue(ParserUtil.hasParser(Double.class));
        Assertions.assertTrue(ParserUtil.hasParser(Boolean.class));
        Assertions.assertTrue(ParserUtil.hasParser(Long.class));
        Assertions.assertTrue(ParserUtil.hasParser(Float.class));
        Assertions.assertTrue(ParserUtil.hasParser(Short.class));
        Assertions.assertTrue(ParserUtil.hasParser(Character.class));
        Assertions.assertTrue(ParserUtil.hasParser(Byte.class));
        
        Assertions.assertFalse(ParserUtil.hasParser(ParserUtilTest.class));
    }
    
    @Test
    void testGetParser() throws ParserNotFoundException {
        Parser<String> stringParser = ParserUtil.getParser(String.class);
        Assertions.assertNotNull(stringParser);
        
        Parser<Integer> intParser = ParserUtil.getParser(Integer.class);
        Assertions.assertNotNull(intParser);
        
        Assertions.assertThrows(ParserNotFoundException.class, () -> ParserUtil.getParser(ParserUtilTest.class));
    }
    
    @Test
    void testFindParserOrNull() {
        Parser<String> stringParser = ParserUtil.findParserOrNull(String.class);
        Assertions.assertNotNull(stringParser);
        
        Parser<ParserUtilTest> nullParser = ParserUtil.findParserOrNull(ParserUtilTest.class);
        Assertions.assertNull(nullParser);
    }
    
    @Test
    void testParseOrNull() {
        String stringResult = ParserUtil.parseOrNull(String.class, "test");
        Assertions.assertEquals("test", stringResult);
        
        Integer intResult = ParserUtil.parseOrNull(Integer.class, "123");
        Assertions.assertEquals(123, intResult);
        
        Integer nullResult = ParserUtil.parseOrNull(Integer.class, "abc");
        Assertions.assertNull(nullResult);
        
        Object nullClassResult = ParserUtil.parseOrNull(ParserUtilTest.class, "test");
        Assertions.assertNull(nullClassResult);
    }
    
    @Test
    void testAddParser() throws ParseException {
        class TestClass {
            private final String value;
            
            public TestClass(String value) {
                this.value = value;
            }
            
            @Override
            public String toString() {
                return value;
            }
        }
        
        Parser<TestClass> testParser = new Parser<>() {
            @Override
            public TestClass parse(String s) {
                return new TestClass(s);
            }
        };
        
        ParserUtil.addParser(TestClass.class, testParser);
        
        Assertions.assertTrue(ParserUtil.hasParser(TestClass.class));
        
        TestClass result = ParserUtil.parse(TestClass.class, "test");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("test", result.toString());
    }
}