/*
 * MIT License
 *
 * Copyright (c) 2018 Alexander Grün
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.alexgruen.query.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alex on 15.03.2016.
 */
public class StringUtil {

    private StringUtil() {
    }


    private static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+([\\.,][0-9]+)?");

    /**
     * Creates a string by repeating a specified char
     * @param length length of resulting string
     * @param indentChar input char
     * @return result string
     */
    public static String repeatChar(int length, char indentChar){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++){
            sb.append(indentChar);
        }
        return sb.toString();
    }

    /**
     * Returns parsed Number if the input string is a valid number representation.
     * If the string can not be parsed null is returned.
     * @param value input string
     * @return parsed number or null
     */
    public static Number toNumberIfValid(String value) {
        Matcher m = NUMBER_PATTERN.matcher(value);
        if (m.matches()) {
            String n = m.group();
            n = n.replace(",", "");
            if (n.contains(".")) {
                return Double.parseDouble(n);
            } else {
                return Long.parseLong(n);
            }
        }
        return null;
    }

    /**
     * Returns true if the input string is a valid number representation
     * @param value input string
     * @return true if number
     */
    public static boolean isNumber(String value) {
        Matcher m = NUMBER_PATTERN.matcher(value);
        return m.matches();
    }

    /**
     * Returns true if the string is quoted ("string" or 'string')
     * @param val input string
     * @return true if quoted
     */
    public static boolean isQuoted(String val) {
        return (val.startsWith("'") && val.endsWith("'"))
                || (val.startsWith("\"") && val.endsWith("\""));
    }

    /**
     * Removes quotes from a string ("string" -> string)
     * @param val input string
     * @return string without quotes
     */
    public static String stripQuotes(String val) {
        boolean quoted = val.startsWith("'") && val.endsWith("'");
        char quoteChar = quoted ? '\'' : '"';
        quoted = quoted ? quoted : val.startsWith("\"") && val.endsWith("\"");
        if (quoted) {

            val = val.replace("\\" + quoteChar, String.valueOf(quoteChar));
            return val.substring(1, val.length() - 1);
        }
        return val;
    }

    /**
     * Returns true if a string value requires quotation.
     * "val 1" -> true, "val1" -> false
     * @param value input string
     * @return true if quotation is required
     */
    public static boolean requiresQuotation(String value) {
        return !isQuoted(value) &&
                (
                        value.contains(" ") ||
                        value.contains("\t") ||
                        value.contains("\n")
                );
    }

    /**
     * Puts a string in quotes.
     * All occurrences of quotes chars in the string are escaped.
     *
     * @param input     string to put in quotes
     * @param quoteChar quote char
     * @return string between quote chars
     */
    public static String putInQuotes(String input, Character quoteChar) {
        return quoteChar + input.replace(quoteChar.toString(), "\\" + quoteChar) + quoteChar;
    }

    /**
     * Split an input string at a specified split-character  into several parts.
     * <tt>"</tt> and <tt>'</tt> are considered during the process.
     * <p><code>"testA    testB   testB" -&gt; [testA,testB,testC]</code></p>
     * <p><code>"'testA    testB'   testB" -&gt; [testA    testB,testC]</code></p>
     *
     * @param input input string
     * @param split char used to split
     * @return string array containing all splitted parts
     */
    public static String[] splitQuoted(String input, Character split) {
        List<String> parts = new ArrayList<>();
        splitQuoted(input, split, parts);
        String[] result = new String[parts.size()];
        return parts.toArray(result);
    }


    /**
     * Split an input string at a specified split-character  into several parts.
     * <tt>"</tt> and <tt>'</tt> are considered during the process.
     * <p><code>"testA    testB   testB" -&gt; [testA,testB,testC]</code></p>
     * <p><code>"'testA    testB'   testB" -&gt; [testA    testB,testC]</code></p>
     *
     * @param input input string
     * @param split char used to split
     * @param parts list filled with the resulting parts
     */
    @SuppressWarnings("ConstantConditions")
    public static void splitQuoted(String input, Character split, List<String> parts) {
        if (input.length() == 0) {
            return;
        }
        boolean inQuotation = false;
        boolean inDoubleQuotation = false;
        boolean escapeNext = false;
        char c;
        boolean startOrSplit = true;
        final StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            if (escapeNext) {
                sb.append(c);
                escapeNext = false;
                continue;
            } else if (c == '\\') {
                escapeNext = true;
                continue;
            } else if (c == '\'') {
                if (inQuotation) {
                    inQuotation = false;
                } else if (!inDoubleQuotation && startOrSplit) {
                    inQuotation = true;
                    startOrSplit = false;
                } else {
                    sb.append(c);
                }
                continue;
            } else if (c == '\"') {
                if (inDoubleQuotation) {
                    inDoubleQuotation = false;
                } else if (!inDoubleQuotation && startOrSplit) {
                    inDoubleQuotation = true;
                    startOrSplit = false;
                } else {
                    sb.append(c);
                }
                continue;
            } else if (i != 0 && c == split && !inDoubleQuotation && !inQuotation) {

                parts.add(sb.toString());
                sb.setLength(0);
                startOrSplit = true;
                continue;
            } else {
                startOrSplit = false;
            }
            sb.append(c);

        }
        parts.add(sb.toString());
    }
}
