package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.datatypes.GObject;
import model.Expression;

public class StructFieldDeclarationEvaluator implements ExpressionEvaluator<Expression.StructFieldDeclaration> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.StructFieldDeclaration expression) {
        return interpreter.evaluateExpression(expression.initializer());
    }
}
