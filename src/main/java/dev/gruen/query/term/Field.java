// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.term;

import dev.gruen.query.util.StringUtil;

import java.util.Arrays;
import java.util.Objects;

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
     *
     * @param fullPath full path
     * @param path     path array
     */
    public Field(String fullPath, String... path) {
        this.fullPath = fullPath;
        this.path = path;
    }

    /**
     * Creates a field from a single field name.
     * In this case, the path array has only one entry
     *
     * @param name field name
     */
    public Field(String name) {
        this(name, name);
    }

    /**
     * Returns the full path string (e.g. "test.field")
     *
     * @return path string
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * Returns the path array (e.g. ["test", "field"])
     *
     * @return path array
     */
    public String[] getPath() {
        return path;
    }

    /**
     * Sets the full path
     *
     * @param fullPath full path
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * Sets the path array
     *
     * @param path path array
     */
    public void setPath(String[] path) {
        this.path = path;
    }

    /**
     * Creates the joinend path string from a path array.
     * <code> from </code> and <code>to</code> can be used to create the path string only from a certain range in the array
     * <p>
     * e.g. ["test", "field 1"] -> "test.'field 1'"
     * from : 1, to: 2 : ["test","field 1", "subfield 2", "x"] -> "'field 1'.'subfield 2'"
     *
     * @param path path array
     * @param from from
     * @param to   to
     * @return path string
     */
    public static String toJoinedPath(String[] path, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < to; i++) {
            String p = path[i];
            if (!sb.isEmpty()) {
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
     *
     * @return path length
     */
    public int getLength() {
        return path.length;
    }

    /**
     * Returns the part of the path array at an specified index
     *
     * @param index path index
     * @return path part
     */
    public String getPart(int index) {
        return path[index];
    }

    /**
     * Returns the joined path for this field ({@link #toJoinedPath(String[], int, int)})
     *
     * @return joined path string
     */
    public String getJoinedPath() {
        return getJoinedPath(0, path.length);
    }

    /**
     * Returns the joined path for this field starting at a specified index ({@link #toJoinedPath(String[], int, int)})
     *
     * @param from start index
     * @return joined path string
     */
    public String getJoinedPath(int from) {
        return getJoinedPath(from, path.length);
    }

    /**
     * Returns the joined path for this field at a specified range from the path array ({@link #toJoinedPath(String[], int, int)})
     *
     * @param from start index
     * @param to   end index (exclusive)
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
     *
     * @return joined path
     */
    @Override
    public String toString() {
        return getJoinedPath();
    }

    /**
     * Two fields are equal if their full path and path array are equal
     *
     * @param o other object
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Field other = (Field) o;
        return Objects.equals(fullPath, other.fullPath) && Arrays.equals(path, other.path);
    }

    /**
     * Calculates the hashcode of this field, consistent with {@link #equals(Object)}
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(fullPath) + Arrays.hashCode(path);
    }
}
