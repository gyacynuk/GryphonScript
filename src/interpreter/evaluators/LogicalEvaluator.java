package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.data.GObject;
import model.Expression;

import static model.TokenType.OR;

public class LogicalEvaluator implements ExpressionEvaluator<Expression.Binary.Logical> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Binary.Logical expression) {
        GObject left = interpreter.evaluateExpression(expression.left());

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
