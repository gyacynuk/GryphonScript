package interpreter;

import interpreter.datatypes.GObject;
import interpreter.lambda.Invokable;
import interpreter.runtime.Environment;
import model.Expression;
import model.Token;

import java.util.List;
import java.util.function.Supplier;

public interface Interpreter {
    GObject executeProgram(List<Expression> expressions);
    GObject evaluateExpression(Expression expression);
    GObject evaluateExpressionInNewScope(Supplier<GObject> expressionEvaluator);
    GObject evaluateExpressionInGivenScope(Supplier<GObject> expressionEvaluator, Environment scope);
    Environment getCurrentScope();
    void resolveStackVariableAtDepth(Expression expression, int depth);
    void assignStackVariable(Expression.Assignment expression, GObject value);
    GObject lookUpStackVariable(Expression.Variable variable);
    GObject invokeLambda(Invokable invokable, List<GObject> arguments, Token token);
}
