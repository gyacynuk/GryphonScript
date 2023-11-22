package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.errors.RuntimeError;
import model.Expression;

public class UnaryEvaluator implements ExpressionEvaluator<Expression.Unary> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Unary expression) {
        Object right = interpreter.evaluateExpression(expression.right());

        var numericDecorator = InterpreterUtils.numericEnforcementFunctionDecorator(expression.operator(), right);
        return switch (expression.operator().type()) {
            case BANG -> !InterpreterUtils.isTruthy(right);
            case MINUS -> numericDecorator.apply(r -> -r) ;
            default -> throw new RuntimeError(expression.operator(), "Unknown unary operator");
        };
    }
}
