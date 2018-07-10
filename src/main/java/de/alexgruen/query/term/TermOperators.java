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

package de.alexgruen.query.term;

import java.util.*;

public class TermOperators {
    /**
     * Equals operator
     */
    public static final TermOperator EQ = new TermOperator("==","EQ", "eq", "=");

    /**
     * Not equals operator
     */
    public static final TermOperator NE = new TermOperator("!=","NE", "ne");

    /**
     * Lower equal operator
     */
    public static final TermOperator LE = new TermOperator("<=","LE", "le");

    /**
     * Lower than operator
     */
    public static final TermOperator LT = new TermOperator("<", "LT","lt");

    /**
     * Larger equal operator
     */
    public static final TermOperator GE = new TermOperator(">=","GE", "ge");

    /**
     * Larger than operator
     */
    public static final TermOperator GT = new TermOperator(">","GT", "gt");

    /**
     * Text compare operator
     */
    public static final TermOperator TEXT = new TermOperator("*=","TEXT", "text");

    /**
     * Regex operator
     */
    public static final TermOperator REGEX = new TermOperator("~=","REGEX", "regex");

    /**
     * Dummy operator for text search terms
     */
    public static final TermOperator FULL_TEXT = new TermOperator("FULL_TEXT");

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
                    FULL_TEXT
            };


    /**
     * Returns a list with all default operators
     * @return default operators
     */
    public static List<TermOperator> getDefaultOperators() {
        return new ArrayList<>(Arrays.asList(DEFAULT_OPERATORS));
    }

    /**
     * Contains all names an aliases of the default operators
     */
    private static Set<String> DEFAULT_ALIASES = new HashSet<>();
    static{

        //Initializes all default names and aliases
        for(TermOperator op : DEFAULT_OPERATORS){
            DEFAULT_ALIASES.add(op.getName());
            DEFAULT_ALIASES.addAll(Arrays.asList(op.getAliases()));
        }
    }

    /**
     * Returns true if a given string is name or alias of a default operator
     * @param name input name
     * @return true if name or alias of default operator
     */
    public static boolean isDefaultAlias(String name){
        return DEFAULT_ALIASES.contains(name);
    }
}
