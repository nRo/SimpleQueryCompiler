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

import de.alexgruen.query.util.StringUtil;

/**
 * Represents the field in a query term (field operator value)
 */
public class Field {
    public static final Field ALL_FIELDS = new Field("*");
    private String fullPath;
    private String[] path;
    private String joinedPath;

    /**
     * Creates a field from a path string and a path array.
     * E.g.: "test.field" , ["test","field"]
     * @param fullPath full path
     * @param path path array
     */
    public Field(String fullPath, String... path) {
        this.fullPath = fullPath;
        this.path = path;
    }

    /**
     * Creates a field from a single field name.
     * In this case, the path array has only one entry
     * @param name field name
     */
    public Field(String name) {
        this(name, name);
    }

    /**
     * Returns the full path string (e.g. "test.field")
     * @return path string
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * Returns the path array (e.g. ["test", "field"]
     * @return
     */
    public String[] getPath() {
        return path;
    }

    /**
     * Sets the full path
     * @param fullPath full path
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * Sets the path array
     * @param path
     */
    public void setPath(String[] path) {
        this.path = path;
    }

    /**
     * Creates the joinend path string from a path array.
     * <tt> from </tt> and <tt>to</tt> can be used to create the path string only from a certain range in the array
     *
     * e.g. ["test", "field 1"] -> "test.'field 1'"
     * from : 1, to: 2 : ["test","field 1", "subfield 2", "x"] -> "'field 1'.'subfield 2'"
     *
     * @param path path array
     * @param from from
     * @param to to
     * @return path string
     */
    public static String toJoinedPath(String[] path, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < to; i++) {
            String p = path[i];
            if (sb.length() > 0) {
                sb.append(".");
            }
            if (StringUtil.requiresQuotation(p)) {
                p = StringUtil.putInQuotes(p, '\'');
            }
            sb.append(p);
        }
        return sb.toString();
    }

    /**
     * Returns the length of the path array
     * @return
     */
    public int getLength(){
        return path.length;
    }

    /**
     * Returns the part of the path array at an specified index
     * @param index path index
     * @return path part
     */
    public String getPart(int index){
        return path[index];
    }

    /**
     * Returns the joined path for this field ({@link #toJoinedPath(String[], int, int)})
     * @return joined path string
     */
    public String getJoinedPath(){
        return getJoinedPath(0,path.length);
    }

    /**
     * Returns the joined path for this field starting at a specified index ({@link #toJoinedPath(String[], int, int)})
     * @return joined path string
     */
    public String getJoinedPath(int from){
        return getJoinedPath(from,path.length);
    }

    /**
     * Returns the joined path for this field at a specified range from the path array ({@link #toJoinedPath(String[], int, int)})
     * @return joined path string
     */
    public String getJoinedPath(int from, int to) {
        if (joinedPath != null) {
            return joinedPath;
        }
        joinedPath = toJoinedPath(path, from, to);
        return joinedPath;
    }

    /**
     * Returns the joined path  ({@link #getJoinedPath()})
     * @return joined path
     */
    @Override
    public String toString() {
        return getJoinedPath();
    }
}
