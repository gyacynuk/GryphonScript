package desugarer;

import model.BinaryExpressionInitializer;
import model.Expression;

import java.util.List;

public abstract class BaseDesugarer implements Desugarer {
    public List<Expression> desugarAll(List<Expression> expressions) {
        return expressions.stream()
                .map(this::desugarExpression)
                .toList();
    }

    protected Expression desugarExpression(Expression expression) {
        return switch (expression) {
            case Expression.Literal literal -> desugarLiteral(literal);
            case Expression.ListLiteral listLiteral -> desugarListLiteral(listLiteral);
            case Expression.StructFieldDeclaration structFieldDeclaration -> desugarStructFieldDeclaration(structFieldDeclaration);
            case Expression.StructLiteral structLiteral -> desugarStructLiteral(structLiteral);
            case Expression.Assignment assignment -> desugarAssignment(assignment);
            case Expression.IndexAssignment indexAssignment -> desugarIndexAssignment(indexAssignment);
            case Expression.Index index -> desugarIndex(index);
            case Expression.Declaration declaration -> desugarDeclaration(declaration);
            case Expression.Variable variable -> desugarVariable(variable);
            case Expression.Group group -> desugarGroup(group);
            case Expression.Unary unary -> desugarUnary(unary);
            case Expression.Binary binary -> desugarBinary(binary);
            case Expression.Block block -> desugarBlock(block);
            case Expression.If ifExpression -> desugarIf(ifExpression);
            case Expression.While whileExpression -> desugarWhile(whileExpression);
            case Expression.Invocation invocation -> desugarInvocation(invocation);
            case Expression.Lambda lambda -> desugarLambda(lambda);
            case null -> null;
        };
    }

    protected Expression desugarLiteral(Expression.Literal literal) {
        return literal;
    }

    protected Expression desugarListLiteral(Expression.ListLiteral listLiteral) {
        return new Expression.ListLiteral(
                desugarAll(listLiteral.values()),
                listLiteral.closingBracket());
    }

    protected Expression desugarStructFieldDeclaration(Expression.StructFieldDeclaration structFieldDeclaration) {
        return new Expression.StructFieldDeclaration(
                structFieldDeclaration.variable(),
                desugarExpression(structFieldDeclaration.initializer()));
    }

    protected Expression desugarStructLiteral(Expression.StructLiteral structLiteral) {
        return new Expression.StructLiteral(
                desugarAll(structLiteral.fields()),
                structLiteral.closingBracket());
    }

    protected Expression desugarAssignment(Expression.Assignment assignment) {
        return new Expression.Assignment(
                assignment.variable(),
                desugarExpression(assignment.value()));
    }

    protected Expression desugarIndexAssignment(Expression.IndexAssignment indexAssignment) {
        return new Expression.IndexAssignment(
                desugarExpression(indexAssignment.assignee()),
                desugarExpression(indexAssignment.index()),
                desugarExpression(indexAssignment.value()),
                indexAssignment.closingBracket());
    }

    protected Expression desugarIndex(Expression.Index index) {
        return new Expression.Index(
                desugarExpression(index.callee()),
                index.closingBracketOrDot(),
                desugarExpression(index.index()));
    }

    protected Expression desugarDeclaration(Expression.Declaration declaration) {
        return new Expression.Declaration(
                declaration.variable(),
                desugarExpression(declaration.initializer()));
    }

    protected Expression desugarVariable(Expression.Variable variable) {
        return variable;
    }

    protected Expression desugarGroup(Expression.Group group) {
        return new Expression.Group(desugarExpression(group.expression()));
    }

    protected Expression desugarUnary(Expression.Unary unary) {
        return new Expression.Unary(unary.operator(), desugarExpression(unary.right()));
    }

    protected Expression desugarBinary(Expression.Binary binary) {
        return BinaryExpressionInitializer.getInitializerForExpression(binary).apply(
                new BinaryExpressionInitializer.Args(
                        binary.left(),
                        binary.right(),
                        binary.operator()));
    }

    protected Expression desugarBlock(Expression.Block block) {
        return new Expression.Block(desugarAll(block.expressions()));
    }

    protected Expression desugarIf(Expression.If ifExpression) {
        return new Expression.If(
                desugarExpression(ifExpression.condition()),
                desugarExpression(ifExpression.thenBranch()),
                desugarExpression(ifExpression.elseBranch()));
    }

    protected Expression desugarWhile(Expression.While whileExpression) {
        return new Expression.While(
                desugarExpression(whileExpression.condition()),
                desugarExpression(whileExpression.body()));
    }

    protected Expression desugarInvocation(Expression.Invocation invocation) {
        return new Expression.Invocation(
                desugarExpression(invocation.callee()),
                invocation.closingBracket(),
                desugarAll(invocation.arguments()));
    }

    protected Expression desugarLambda(Expression.Lambda lambda) {
        return new Expression.Lambda(
                lambda.parameters(),
                desugarExpression(lambda.body()));
    }
}
