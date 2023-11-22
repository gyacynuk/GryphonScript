package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.errors.RuntimeError;
import interpreter.lambda.Invokable;
import model.Expression;

import java.util.List;

public class InvocationEvaluator implements ExpressionEvaluator<Expression.Invocation> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Invocation expression) {
        Object callee = interpreter.evaluateExpression(expression.callee());

        List<Object> arguments = expression.arguments().stream()
                .map(interpreter::evaluateExpression)
                .toList();

        if (callee instanceof Invokable invokable) {
            return interpreter.invokeLambda(invokable, arguments, expression.closingBracket());
        }

        throw new RuntimeError(expression.closingBracket(), "Can only call functions and classes");
    }
}
