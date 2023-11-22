package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.data.GObject;
import model.Expression;

public interface ExpressionEvaluator<T extends Expression> {
    GObject evaluateExpression(Interpreter interpreter, T expression);
}
