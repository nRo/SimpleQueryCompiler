package de.alexgruen.query.compiler.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

class ParserTest {

    @Test
    void testParse() throws ParseException {
        TestParser parser = new TestParser();
        Integer result = parser.parse("123");
        Assertions.assertEquals(123, result);
        
        Assertions.assertThrows(ParseException.class, () -> parser.parse("abc"));
    }
    
    @Test
    void testParseOrNull() {
        TestParser parser = new TestParser();
        Integer result = parser.parseOrNull("456");
        Assertions.assertEquals(456, result);
        
        Integer nullResult = parser.parseOrNull("abc");
        Assertions.assertNull(nullResult);
    }
    
    private static class TestParser extends Parser<Integer> {
        @Override
        public Integer parse(String s) throws ParseException {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new ParseException("Failed to parse: " + s, 0);
            }
        }
    }
}