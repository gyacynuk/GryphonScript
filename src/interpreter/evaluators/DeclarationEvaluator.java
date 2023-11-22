package interpreter.evaluators;

import interpreter.Interpreter;
import model.Expression;

public class DeclarationEvaluator implements ExpressionEvaluator<Expression.Declaration> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Declaration expression) {
        Object value = expression.initializer() == null
                ? null
                : interpreter.evaluateExpression(expression.initializer());
        interpreter.getCurrentScope().define(expression.variable().lexeme(), value);
        return value;
    }
}
