package model;

import interpreter.datatypes.GHole;
import interpreter.datatypes.GNil;
import interpreter.datatypes.GObject;

import java.util.List;

public sealed interface Expression permits Expression.Assignment, Expression.Binary, Expression.Block, Expression.Declaration, Expression.Group, Expression.If, Expression.Index, Expression.IndexAssignment, Expression.Invocation, Expression.Lambda, Expression.ListLiteral, Expression.Literal, Expression.StructFieldDeclaration, Expression.StructLiteral, Expression.Unary, Expression.Variable, Expression.While, SugarExpression {
    Expression.Literal HOLE = new Literal(GHole.INSTANCE);
    Expression.Literal NIL = new Literal(GNil.INSTANCE);

    /**
     * A literal value, which can take the form of:
     *   → INTEGER| DOUBLE | STRING | "true" | "false" | "nil"
     *
     * @param value the literal value represented in Java. For instance, a Java String, or a Java Boolean.
     */
    record Literal(GObject value) implements Expression {}
    record ListLiteral(List<Expression> values, Token closingBracket) implements Expression {}
    record StructFieldDeclaration(Token variable, Expression initializer) implements Expression {}
    record StructLiteral(List<Expression> fields, Token closingBracket) implements Expression {}
    record Variable(Token name) implements Expression {}
    record Declaration(Token variable, Expression initializer) implements Expression {}
    record Assignment(Token variable, Expression value) implements Expression {}
    record IndexAssignment(Expression assignee, Expression index, Expression value, Token closingBracket) implements Expression {}
    record Unary(Token operator, Expression right) implements Expression {}
    sealed interface Binary extends Expression {
        Expression left();
        Expression right();
        Token operator();
        record Operation(Expression left, Expression right, Token operator) implements Binary { }
        record Logical(Expression left, Expression right, Token operator) implements Binary { }
        record Infix(Expression left, Expression right, Token operator) implements Binary { }
    }

    /**
     * A group of expressions with the highest precedence for evaluation. For instance, (1 + 2) is a grouping in the
     * expression: (1 + 2) * 3
     *
     * @param expression
     */
    record Group(Expression expression) implements Expression {}
    record Block(List<Expression> expressions) implements Expression {}
    record If(Expression condition, Expression thenBranch, Expression elseBranch) implements Expression {}
    record While(Expression condition, Expression body) implements Expression {}
    record Lambda(List<Token> parameters, Expression body, boolean combinable) implements Expression {}
    record Invocation(Expression callee, Token closingBracket, List<Expression> arguments) implements Expression {}
    record Index(Expression callee, Token closingBracketOrDot, Expression index) implements Expression {}
}
