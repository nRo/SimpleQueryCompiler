// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.creator;

import dev.gruen.query.QueryNode;
import dev.gruen.query.term.Field;
import dev.gruen.query.term.Value;

public interface TermCreator<T> extends OperatorCreator {
    /**
     * Crates a Query of the desired type from a {@link Field} and {@link Value}
     *
     * @param node  query node
     * @param field input field
     * @param value input value
     * @return query
     */
    T create(QueryNode node, Field field, Value value);
}
