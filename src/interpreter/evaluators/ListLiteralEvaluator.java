package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.datatypes.GList;
import interpreter.datatypes.GObject;
import model.Expression;

import java.util.ArrayList;
import java.util.List;

public class ListLiteralEvaluator implements ExpressionEvaluator<Expression.ListLiteral> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.ListLiteral expression) {
        List<GObject> backingList = new ArrayList<>();
        expression.values().forEach(elementExpression -> backingList.add(interpreter.evaluateExpression(elementExpression)));
        return new GList(backingList);
    }
}
