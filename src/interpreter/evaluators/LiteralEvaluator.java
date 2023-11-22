package interpreter.evaluators;

import interpreter.Interpreter;
import model.Expression;

public class LiteralEvaluator implements ExpressionEvaluator<Expression.Literal> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Literal expression) {
        return expression.value();
    }
}
