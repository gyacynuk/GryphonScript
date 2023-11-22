package interpreter.evaluators;

import error.Result;
import interpreter.Interpreter;
import interpreter.datatypes.GIndexable;
import interpreter.datatypes.GObject;
import interpreter.errors.RuntimeError;
import model.Expression;

public class IndexEvaluator implements ExpressionEvaluator<Expression.Index> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Index expression) {
        GObject callee = interpreter.evaluateExpression(expression.callee());
        GObject index = interpreter.evaluateExpression(expression.index());

        if (!(callee instanceof GIndexable indexable)) {
            throw new RuntimeError(expression.closingBracket(), "Cannot index a primitive data type or lambda, only lists and structs can be indexed");
        }

        Result<GObject, String> result = indexable.getAtIndex(index);
        return switch (result) {
            case Result.Success<GObject, String> success -> success.value();
            case Result.Error<GObject, String> error -> throw new RuntimeError(expression.closingBracket(), error.value());
        };
    }
}
