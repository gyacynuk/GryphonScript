package model;

import java.util.List;

public sealed interface SugarExpression extends Expression {
    Token getErrorReportingToken();

    // TODO: these interfaces are now useless, extract inner classes to this level
    sealed interface ArrayDestructureField extends SugarExpression {
        record FieldDeclaration(Token nullableVariable, List<Expression> context, Destructure nullableNestedDestructure) implements ArrayDestructureField {
            @Override
            public Token getErrorReportingToken() {
                return nullableVariable == null
                        ? new Token(TokenType.NIL, "generated-token", null, -1, false)
                        : nullableVariable;
            }
        }
    }
    sealed interface StructDestructureField extends SugarExpression {
        record FieldDeclaration(Token variable, List<Expression> context, Destructure nullableNestedDestructure) implements StructDestructureField {
            @Override
            public Token getErrorReportingToken() {
                return variable;
            }
        }
    }
    sealed interface Destructure extends SugarExpression {
        List<? extends SugarExpression> fields();
        record ArrayDestructure(List<ArrayDestructureField> fields, Token closingBracket) implements Destructure {
            @Override
            public Token getErrorReportingToken() {
                return closingBracket;
            }
        }
        record StructDestructure(List<StructDestructureField> fields, Token closingBracket) implements Destructure {
            @Override
            public Token getErrorReportingToken() {
                return closingBracket;
            }
        }
    }
    record DestructureDeclaration(Destructure destructureExpression, Token equals, Expression initializer) implements SugarExpression {
        @Override
        public Token getErrorReportingToken() {
            return equals;
        }
    }
}
