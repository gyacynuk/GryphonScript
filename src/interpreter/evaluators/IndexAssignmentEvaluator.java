package interpreter.evaluators;

import error.Result;
import interpreter.Interpreter;
import interpreter.datatypes.GIndexable;
import interpreter.datatypes.GObject;
import interpreter.errors.RuntimeError;
import model.Expression;

public class IndexAssignmentEvaluator implements ExpressionEvaluator<Expression.IndexAssignment> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.IndexAssignment expression) {
        GObject assignee = interpreter.evaluateExpression(expression.assignee());
        GObject index = interpreter.evaluateExpression(expression.index());
        GObject value = interpreter.evaluateExpression(expression.value());

        if (!(assignee instanceof GIndexable indexable)) {
            throw new RuntimeError(expression.closingBracket(), "Cannot index a primitive data type, only lists and structs can be indexed");
        }

        Result<GObject, String> result = indexable.setAtIndex(index, value);
        return switch (result) {
            case Result.Success<GObject, String> success -> success.value();
            case Result.Error<GObject, String> error -> throw new RuntimeError(expression.closingBracket(), error.value());
        };
    }
}
