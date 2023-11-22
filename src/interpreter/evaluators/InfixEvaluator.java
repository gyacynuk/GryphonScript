package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.data.GLambda;
import interpreter.data.GObject;
import interpreter.errors.RuntimeError;
import interpreter.lambda.Invokable;
import model.Expression;

import java.util.Collections;

public class InfixEvaluator implements ExpressionEvaluator<Expression.Binary.Infix> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Binary.Infix expression) {
        GObject left = interpreter.evaluateExpression(expression.left());
        GObject right = interpreter.evaluateExpression(expression.right());

        if (right instanceof GLambda gLambda) {
            return interpreter.invokeLambda(gLambda.value(), Collections.singletonList(left), expression.operator());
        }

        throw new RuntimeError(expression.operator(), "Infix operator '|>' must have a lambda as the second operand");
    }
}
