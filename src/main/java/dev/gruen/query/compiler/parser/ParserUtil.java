// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler.parser;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for parsing Strings to objects, using the registered {@link Parser}s.
 */
public class ParserUtil {
    private ParserUtil() {
        throw new IllegalStateException("utility class");
    }

    /**
     * Contains all parsers assigned for different target types
     */
    private static final Map<Class<?>, Parser<?>> parserMap = new ConcurrentHashMap<>();

    static {
        init();
    }

    private static Map<Class<?>, Parser<?>> getParserMap() {
        return parserMap;
    }

    /**
     * Adds the default parsers
     */
    private static void init() {
        parserMap.put(String.class, (Parser<String>) s -> s);
        parserMap.put(Double.class, (Parser<Double>) Double::parseDouble);
        parserMap.put(Integer.class, (Parser<Integer>) Integer::parseInt);
        parserMap.put(Float.class, (Parser<Float>) Float::parseFloat);
        parserMap.put(Long.class, (Parser<Long>) Long::parseLong);
        parserMap.put(Short.class, (Parser<Short>) Short::parseShort);
        parserMap.put(Byte.class, (Parser<Byte>) Byte::parseByte);
        parserMap.put(Character.class, (Parser<Character>) s -> s.charAt(0));
        parserMap.put(Boolean.class, (Parser<Boolean>) ParserUtil::parseBoolean);
    }

    /**
     * Parses a boolean. Accepted values are <code>true</code>, <code>false</code>, <code>t</code> and <code>f</code>
     * (case insensitive).
     */
    private static Boolean parseBoolean(String s) throws ParseException {
        if (s == null) {
            throw new ParseException("illegal boolean value: null", 0);
        }
        return switch (s.toLowerCase()) {
            case "true", "t" -> true;
            case "false", "f" -> false;
            default -> throw new ParseException(String.format("illegal boolean value: %s", s), 0);
        };
    }


    /**
     * Parses a String into an array. The corresponding parser for the array type is used.
     *
     * @param cl  array class (String[].class)
     * @param x   String to parse
     * @param <T> type of array class
     * @return parsed array object
     * @throws ParseException thrown if the string can not be parsed
     */
    private static <T> T parseArray(Class<T> cl, String x) throws ParseException {
        Parser<?> p = getParserMap().get(cl.getComponentType());
        Class<?> cc = cl.getComponentType();
        String[] vals = x.split("[;,|]");
        Object r = Array.newInstance(cc, vals.length);
        for (int i = 0; i < vals.length; i++) {
            Array.set(r, i, p.parse(vals[i]));
        }
        return cl.cast(r);
    }


    /**
     * Parses a String into an Object of a defined type
     *
     * @param cl  class of the returned object
     * @param x   String to parse
     * @param <T> type of the returned object
     * @return parsed object
     * @throws ParseException          thrown if the string can not be parsed
     * @throws ParserNotFoundException thrown if no parser is found for the object type
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(Class<T> cl, String x) throws ParseException, ParserNotFoundException {
        if ((cl.isArray() && !getParserMap().containsKey(cl.getComponentType())) && !getParserMap().containsKey(cl)) {
            throw new ParserNotFoundException(cl);
        }
        try {
            if (cl.isArray()) {
                return parseArray(cl, x);
            }
            Parser<T> p = getParser(cl);
            return p.parse(x);
        } catch (Exception e) {
            throw new ParseException("error parsing '" + x + "' to " + cl.getName(), 0);
        }
    }


    /**
     * Checks if a parser for the class is available
     *
     * @param cl input class
     * @return true if parser is available
     */
    public static boolean hasParser(Class<?> cl) {
        return getParserMap().get(cl) != null;
    }

    /**
     * Returns a parser for the input class
     *
     * @param cl  input class
     * @param <T> type of class
     * @return parser for input class
     * @throws ParserNotFoundException thrown if no parser is found
     */
    @SuppressWarnings("unchecked")
    public static <T> Parser<T> getParser(Class<T> cl) throws ParserNotFoundException {
        Parser<?> parser = getParserMap().get(cl);
        if (parser == null) {
            throw new ParserNotFoundException(cl);
        }
        return (Parser<T>) parser;
    }

    /**
     * Returns a parser for the input class or null if no parser is found
     *
     * @param cl  input class
     * @param <T> type of class
     * @return parser for input class
     */
    public static <T> Parser<T> findParserOrNull(Class<T> cl) {
        try {
            return getParser(cl);
        } catch (ParserNotFoundException ignored) {
            // ignore errors
        }
        return null;
    }

    /**
     * Parses a String into an object of input type.
     * returns null if no parser is found or the String can't be parsed.
     *
     * @param cl  Class of resulting object
     * @param x   String to parse
     * @param <T> type of resulting object
     * @return parsed Object
     */
    public static <T> T parseOrNull(Class<T> cl, String x) {
        if ((cl.isArray() && !getParserMap().containsKey(cl.getComponentType())) && !getParserMap().containsKey(cl)) {
            return null;
        }
        try {
            if (cl.isArray()) {
                return parseArray(cl, x);
            }
            Parser<T> p = getParser(cl);
            return p.parse(x);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Adds a parser for the input class
     *
     * @param c   class of parser
     * @param p   parser
     * @param <T> type of class
     */
    public static <T> void addParser(Class<T> c, Parser<T> p) {
        getParserMap().put(c, p);
    }
}
