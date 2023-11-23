package model;

import java.util.function.Function;

public class BinaryExpressionInitializer {
    public record Args(Expression left, Expression right, Token operator) {}

    public static Function<Args, Expression.Binary> getInitializerForExpression(Expression.Binary expression) {
        return switch (expression) {
            case Expression.Binary.Operation ignored -> BinaryExpressionInitializer::initOperation;
            case Expression.Binary.Logical ignored -> BinaryExpressionInitializer::initLogical;
            case Expression.Binary.Infix ignored -> BinaryExpressionInitializer::initInfix;
        };
    }

    public static Expression.Binary.Operation initOperation(Args args) {
        return new Expression.Binary.Operation(args.left, args.right, args.operator);
    }

    public static Expression.Binary.Logical initLogical(Args args) {
        return new Expression.Binary.Logical(args.left, args.right, args.operator);
    }

    public static Expression.Binary.Infix initInfix(Args args) {
        return new Expression.Binary.Infix(args.left, args.right, args.operator);
    }
}
