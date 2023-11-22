package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.data.GBoolean;
import interpreter.data.GInteger;
import interpreter.data.GObject;
import interpreter.errors.RuntimeError;
import model.Expression;

public class UnaryEvaluator implements ExpressionEvaluator<Expression.Unary> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Unary expression) {
        GObject right = interpreter.evaluateExpression(expression.right());

        var numericDecorator = InterpreterUtils.numericEnforcementFunctionDecorator(expression.operator(), right);
        return switch (expression.operator().type()) {
            case BANG -> new GBoolean(!InterpreterUtils.isTruthy(right));
            case MINUS -> numericDecorator.apply(numeric -> new GInteger(0).subtract(numeric)) ;
            default -> throw new RuntimeError(expression.operator(), "Unknown unary operator");
        };
    }
}
