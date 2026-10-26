// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.term;

import java.util.*;

public class TermOperators {
    private TermOperators() {
        throw new IllegalStateException("utility class");
    }
    /**
     * Equals operator
     */
    public static final TermOperator EQ = new TermOperator("==", "EQ", "eq", "=");

    /**
     * Not equals operator
     */
    public static final TermOperator NE = new TermOperator("!=", "NE", "ne");

    /**
     * Lower equal operator
     */
    public static final TermOperator LE = new TermOperator("<=", "LE", "le");

    /**
     * Lower than operator
     */
    public static final TermOperator LT = new TermOperator("<", "LT", "lt");

    /**
     * Larger equal operator
     */
    public static final TermOperator GE = new TermOperator(">=", "GE", "ge");

    /**
     * Larger than operator
     */
    public static final TermOperator GT = new TermOperator(">", "GT", "gt");

    /**
     * Text compare operator
     */
    public static final TermOperator TEXT = new TermOperator("*=", "TEXT", "text");

    /**
     * Regex operator
     */
    public static final TermOperator REGEX = new TermOperator("~=", "REGEX", "regex");

    /**
     * Dummy operator for text search terms
     */
    public static final TermOperator FULL_TEXT = new TermOperator("FULL_TEXT");

    public static final TermOperator IN = new TermOperator("IN", "in");

    public static final TermOperator NOT_IN = new TermOperator("!IN", "!in");

    /**
     * Contains all default operators
     */
    private static final TermOperator[] DEFAULT_OPERATORS =
            new TermOperator[]{
                    EQ,
                    NE,
                    LE,
                    LT,
                    GE,
                    GT,
                    TEXT,
                    REGEX,
                    FULL_TEXT,
                    IN,
                    NOT_IN
            };


    /**
     * Returns a list with all default operators
     *
     * @return default operators
     */
    public static List<TermOperator> getDefaultOperators() {
        return new ArrayList<>(Arrays.asList(DEFAULT_OPERATORS));
    }

    /**
     * Contains all names an aliases of the default operators
     */
    private static final Set<String> DEFAULT_ALIASES = new HashSet<>();

    static {

        //Initializes all default names and aliases
        for (TermOperator op : DEFAULT_OPERATORS) {
            DEFAULT_ALIASES.add(op.getName());
            DEFAULT_ALIASES.addAll(Arrays.asList(op.getAliases()));
        }
    }

    /**
     * Returns true if a given string is name or alias of a default operator
     *
     * @param name input name
     * @return true if name or alias of default operator
     */
    public static boolean isDefaultAlias(String name) {
        return DEFAULT_ALIASES.contains(name);
    }
}
