// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler.parser;

import java.text.ParseException;

/**
 * Parses a String into an object of type T. Since this is a functional interface, parsers can be
 * written as lambdas or method references, e.g. <code>Double::parseDouble</code>.
 *
 * @param <T> type of the parsed object
 */
@FunctionalInterface
public interface Parser<T> {

    /**
     * Parse input String and returns an object of type T
     *
     * @param s input string
     * @return resulting object
     * @throws ParseException throw if there was an error during parsing
     */
    T parse(String s) throws ParseException;

    /**
     * Parse input String and returns an object of type T.
     * If parsing doesn't work, null is returned
     *
     * @param s input string
     * @return resulting object
     */
    default T parseOrNull(String s) {
        try {
            return parse(s);
        } catch (Exception e) {
            return null;
        }
    }

}
