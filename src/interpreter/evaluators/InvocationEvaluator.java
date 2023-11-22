package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.data.GLambda;
import interpreter.data.GObject;
import interpreter.errors.RuntimeError;
import interpreter.lambda.Invokable;
import model.Expression;

import java.util.List;

public class InvocationEvaluator implements ExpressionEvaluator<Expression.Invocation> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Invocation expression) {
        GObject callee = interpreter.evaluateExpression(expression.callee());

        List<GObject> arguments = expression.arguments().stream()
                .map(interpreter::evaluateExpression)
                .toList();

        if (callee instanceof GLambda gLambda) {
            return interpreter.invokeLambda(gLambda.value(), arguments, expression.closingBracket());
        }

        throw new RuntimeError(expression.closingBracket(), "Can only call functions and classes");
    }
}
