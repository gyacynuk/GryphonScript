package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.errors.RuntimeError;
import interpreter.lambda.Invokable;
import model.Expression;

import java.util.Collections;

public class InfixEvaluator implements ExpressionEvaluator<Expression.Binary.Infix> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Binary.Infix expression) {
        Object left = interpreter.evaluateExpression(expression.left());
        Object right = interpreter.evaluateExpression(expression.right());

        if (right instanceof Invokable invokable) {
            return interpreter.invokeLambda(invokable, Collections.singletonList(left), expression.operator());
        }

        throw new RuntimeError(expression.operator(), "Infix operator '|>' must have a lambda as the second operand");
    }
}
