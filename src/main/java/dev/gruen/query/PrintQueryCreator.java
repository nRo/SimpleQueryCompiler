// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query;

import dev.gruen.query.term.Field;
import dev.gruen.query.term.Value;

/**
 * Default creator for {@link PrintQuery}
 */
public class PrintQueryCreator implements QueryCreator<PrintQuery> {
    public static String value2string(Value value) {
        if (value.isNull()) {
            return null;
        }
        if (value.isField()) {
            return value.getString();
        }
        return value.isString() ? String.format("'%s'", value) : value.toString();
    }

    @Override
    public PrintQuery ne(QueryNode node, Field field, Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s != %s)", field, value2string(value));
            }
        };
    }

    @Override
    public PrintQuery eq(QueryNode node, Field field, Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s == %s)", field, value2string(value));
            }
        };
    }

    @Override
    public PrintQuery gt(QueryNode node, Field field, Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s > %s)", field, value2string(value));
            }
        };
    }

    @Override
    public PrintQuery ge(QueryNode node, Field field, Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s >= %s)", field, value2string(value));
            }
        };
    }

    @Override
    public PrintQuery lt(QueryNode node, Field field, Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s < %s)", field, value2string(value));
            }
        };
    }

    @Override
    public PrintQuery le(QueryNode node, Field field, Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s <= %s)", field, value2string(value));
            }
        };
    }

    @Override
    public PrintQuery regex(QueryNode node, Field field, Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s ~= /%s/)", field, value.toString());
            }
        };
    }

    @Override
    public PrintQuery text(QueryNode node, Field field, Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("(%s *= %s)", field, value2string(value));
            }
        };
    }

    @Override
    public PrintQuery in(QueryNode node, Field field, Value list) {
        return new PrintQuery() {
            @Override
            public String toString() {
                String values = String.join(", ", list.getList().stream().map(PrintQueryCreator::value2string).toList());
                return String.format("(%s in [%s])", field, values);
            }
        };
    }

    @Override
    public PrintQuery notIn(QueryNode node, Field field, Value list) {
        return new PrintQuery() {
            @Override
            public String toString() {
                String values = String.join(", ", list.getList().stream().map(PrintQueryCreator::value2string).toList());
                return String.format("(%s !in [%s])", field, values);
            }
        };
    }

    @Override
    public PrintQuery not(QueryNode node, PrintQuery a) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("!%s", a.toString());
            }
        };
    }


    @Override
    public PrintQuery and(QueryNode node, PrintQuery... p) {

        return new PrintQuery() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                for (int i = 0; i < p.length; i++) {
                    sb.append(p[i].toString());
                    if (i < p.length - 1) {
                        sb.append(" && ");
                    }
                }
                sb.append(")");
                return sb.toString();
            }
        };
    }

    @Override
    public PrintQuery or(QueryNode node, PrintQuery... p) {
        return new PrintQuery() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                for (int i = 0; i < p.length; i++) {
                    sb.append(p[i].toString());
                    if (i < p.length - 1) {
                        sb.append(" || ");
                    }
                }
                sb.append(")");
                return sb.toString();
            }
        };
    }

    @Override
    public PrintQuery xor(QueryNode node, PrintQuery... p) {
        return new PrintQuery() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                for (int i = 0; i < p.length; i++) {
                    sb.append(p[i].toString());
                    if (i < p.length - 1) {
                        sb.append(" XOR ");
                    }
                }
                sb.append(")");
                return sb.toString();
            }
        };
    }

    @Override
    public PrintQuery nor(QueryNode node, PrintQuery... p) {
        return new PrintQuery() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                for (int i = 0; i < p.length; i++) {
                    sb.append(p[i].toString());
                    if (i < p.length - 1) {
                        sb.append(" NOR ");
                    }
                }
                sb.append(")");
                return sb.toString();
            }
        };
    }

    @Override
    public PrintQuery fullSearch(Value value) {
        return new PrintQuery() {
            @Override
            public String toString() {
                return String.format("'%s'", value.toString());
            }
        };
    }

    @Override
    public PrintQuery empty() {
        return new PrintQuery() {
            @Override
            public String toString() {
                return "*";
            }
        };
    }
}
