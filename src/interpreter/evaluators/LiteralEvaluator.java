package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.datatypes.GObject;
import model.Expression;

public class LiteralEvaluator implements ExpressionEvaluator<Expression.Literal> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Literal expression) {
        return expression.value();
    }
}
