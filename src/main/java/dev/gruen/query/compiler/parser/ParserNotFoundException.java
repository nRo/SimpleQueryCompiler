// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler.parser;

import dev.gruen.query.compiler.QueryCompilerException;


public class ParserNotFoundException extends QueryCompilerException {
    public ParserNotFoundException(Class<?> c) {
        super("no parser found for " + c.getName());
    }
}
