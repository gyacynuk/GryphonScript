package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.errors.RuntimeError;
import model.Expression;

import java.util.Objects;

public class BinaryOperationEvaluator implements ExpressionEvaluator<Expression.Binary.Operation> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Binary.Operation expression) {
        Object left = interpreter.evaluateExpression(expression.left());
        Object right = interpreter.evaluateExpression(expression.right());

        var doubleCheck = InterpreterUtils.numericEnforcementBiFunctionDecorator(expression.operator(), left, right);
        return switch (expression.operator().type()) {
            case GREATER -> doubleCheck.apply((l, r) -> l > r);
            case GREATER_EQUAL -> doubleCheck.apply((l, r) -> l >= r);
            case LESS -> doubleCheck.apply((l, r) -> l < r);
            case LESS_EQUAL -> doubleCheck.apply((l, r) -> l <= r);
            case MINUS -> doubleCheck.apply((l, r) -> l - r);
            case SLASH -> doubleCheck.apply((l, r) -> l / r);
            case STAR -> doubleCheck.apply((l, r) -> l * r);
            case PLUS -> {
                if (left instanceof String || right instanceof String) {
                    yield InterpreterUtils.stringify(left) + InterpreterUtils.stringify(right);
                } else if (left instanceof Double l && right instanceof Double r) {
                    yield l + r;
                }
                else {
                    throw new RuntimeError(expression.operator(), "No '+' operator definition for the given operand types");
                }
            }
            case EQUAL_EQUAL -> Objects.equals(left, right);
            case BANG_EQUAL -> !Objects.equals(left, right);
            default -> throw new RuntimeError(expression.operator(), "Unknown binary operator");
        };
    }
}
