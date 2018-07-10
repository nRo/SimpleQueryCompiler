package de.alexgruen.querycompiler;

import de.alexgruen.query.util.StringUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StringTest {

    @Test
    public void fieldParserTest(){

        String[] path = StringUtil.splitQuoted("test",'.');
        Assertions.assertArrayEquals(new String[]{"test"},path);

        path = StringUtil.splitQuoted("'test'",'.');
        Assertions.assertArrayEquals(new String[]{"test"},path);

        path = StringUtil.splitQuoted(".test",'.');
        Assertions.assertArrayEquals(new String[]{".test"},path);

        path = StringUtil.splitQuoted(".test.x",'.');
        Assertions.assertArrayEquals(new String[]{".test","x"},path);

        path = StringUtil.splitQuoted("\"test\"",'.');
        Assertions.assertArrayEquals(new String[]{"test"},path);


        path = StringUtil.splitQuoted("\"te's\\\"t\"",'.');
        Assertions.assertArrayEquals(new String[]{"te's\"t"},path);

        path = StringUtil.splitQuoted("test.x",'.');
        Assertions.assertArrayEquals(new String[]{"test","x"},path);

        path = StringUtil.splitQuoted("test.'x y'",'.');
        Assertions.assertArrayEquals(new String[]{"test","x y"},path);

        path = StringUtil.splitQuoted("\"test path\".'x y'",'.');
        Assertions.assertArrayEquals(new String[]{"test path","x y"},path);
    }
    @Test
    public void stringQuotesTest(){
        Assertions.assertTrue(StringUtil.isQuoted("'test'"));
        Assertions.assertTrue(StringUtil.isQuoted("\"test\""));
        Assertions.assertTrue(StringUtil.isQuoted("\"test test\""));
        Assertions.assertTrue(StringUtil.isQuoted("\"test \"test\""));

        Assertions.assertFalse(StringUtil.isQuoted("test"));
        Assertions.assertFalse(StringUtil.isQuoted("tes\"t"));
        Assertions.assertFalse(StringUtil.isQuoted("t'es't"));
        Assertions.assertFalse(StringUtil.isQuoted("t\"es\"t"));
        Assertions.assertFalse(StringUtil.isQuoted("t\"es't"));
        Assertions.assertFalse(StringUtil.isQuoted("t\"es't'"));
        Assertions.assertFalse(StringUtil.isQuoted("\"t\"es't'"));


        Assertions.assertEquals("'test'", StringUtil.putInQuotes("test",'\''));
        Assertions.assertEquals("'te\\'st'", StringUtil.putInQuotes("te'st",'\''));
        Assertions.assertEquals("test", StringUtil.stripQuotes("'test'"));
        Assertions.assertEquals("te'st", StringUtil.stripQuotes("'te\\'st'"));


        Assertions.assertTrue(StringUtil.requiresQuotation("x x"));
        Assertions.assertTrue(StringUtil.requiresQuotation("x   x"));
        Assertions.assertTrue(StringUtil.requiresQuotation("x   x x"));

        Assertions.assertFalse(StringUtil.requiresQuotation("'x x'"));
        Assertions.assertFalse(StringUtil.requiresQuotation("xx"));
        Assertions.assertFalse(StringUtil.requiresQuotation("x"));
    }

}
