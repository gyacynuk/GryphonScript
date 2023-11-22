package model;

public class BinaryExpressionInitializer {
    public record Args(Expression left, Expression right, Token operator) {}

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
