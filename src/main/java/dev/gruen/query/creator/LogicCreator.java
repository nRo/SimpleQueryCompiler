// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.creator;


import dev.gruen.query.QueryNode;

public interface LogicCreator<T> extends OperatorCreator {

    /**
     * Creates a new term from several input terms.
     *
     * @param node     query node
     * @param children input terms
     * @return new term
     */
    @SuppressWarnings("unchecked")
    T create(QueryNode node, T... children);
}
