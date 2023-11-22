package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import model.Expression;

import static model.TokenType.OR;

public class LogicalEvaluator implements ExpressionEvaluator<Expression.Binary.Logical> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Binary.Logical expression) {
        Object left = interpreter.evaluateExpression(expression.left());

        // Attempt short-circuit OR
        if (expression.operator().type() == OR) {
            if (InterpreterUtils.isTruthy(left)) return left;
        }
        // Attempt short-circuit AND
        else {
            if (!InterpreterUtils.isTruthy(left)) return left;
        }

        return interpreter.evaluateExpression(expression.right());
    }
}
