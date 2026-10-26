// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query;


public class QueryTree {
    private static final QueryPrinter PRINTER = QueryPrinter.create().build();

    private QueryNode root;

    public QueryTree(QueryNode root) {
        this.root = root;
    }

    public QueryNode getRoot() {
        return root;
    }

    public void setRoot(QueryNode root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return PRINTER.toString(root);
    }

}
