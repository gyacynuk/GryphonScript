package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.errors.RuntimeError;
import model.Expression;

import java.util.List;

public class IndexEvaluator implements ExpressionEvaluator<Expression.Index> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Index expression) {
        Object callee = interpreter.evaluateExpression(expression.callee());
        Object index = interpreter.evaluateExpression(expression.index());

        if (index instanceof Integer i) {
            if (callee instanceof List<?> list) {
                if (i < 0) {
                    throw new RuntimeError(expression.closingBracket(), String.format("Index %d must be non-negative", i));
                } else if (i >= list.size()) {
                    throw new RuntimeError(expression.closingBracket(), String.format("Index %d out of bounds for list of length %d", i, list.size()));
                }
                return list.get(i);
            } else {
                return null;
            }
        } else {
            throw new RuntimeError(expression.closingBracket(), "Index must be an integer");
        }
    }
}
