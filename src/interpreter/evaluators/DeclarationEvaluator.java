package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.datatypes.GObject;
import model.Expression;

public class DeclarationEvaluator implements ExpressionEvaluator<Expression.Declaration> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Declaration expression) {
        GObject value = expression.initializer() == null
                ? null
                : interpreter.evaluateExpression(expression.initializer());
        interpreter.getCurrentScope().define(expression.variable().lexeme(), value);
        return value;
    }
}
