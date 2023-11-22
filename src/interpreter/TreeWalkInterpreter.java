package interpreter;

import error.ErrorReporter;
import interpreter.errors.RuntimeError;
import interpreter.evaluators.*;
import interpreter.lambda.Invokable;
import interpreter.nativeFunctions.NativeFunctions;
import interpreter.runtime.Environment;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import model.Expression;
import model.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Singleton
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class TreeWalkInterpreter implements Interpreter {
    private final ErrorReporter errorReporter;
    private final NativeFunctions nativeFunctions;
    private final Environment globalEnvironment = Environment.createGlobalEnvironment();
    private final Map<Expression, Integer> stackDepthMap = new HashMap<>();
    private Environment currentEnvironment = globalEnvironment;

    private final LiteralEvaluator literalEvaluator;
    private final VariableEvaluator variableEvaluator;
    private final DeclarationEvaluator declarationEvaluator;
    private final AssignmentEvaluator assignmentEvaluator;
    private final UnaryEvaluator unaryExecutor;
    private final GroupEvaluator groupEvaluator;
    private final BinaryOperationEvaluator binaryOperationEvaluator;
    private final LogicalEvaluator logicalEvaluator;
    private final InfixEvaluator infixEvaluator;
    private final BlockEvaluator blockEvaluator;
    private final IfEvaluator ifEvaluator;
    private final WhileEvaluator whileEvaluator;
    private final LambdaEvaluator lambdaEvaluator;
    private final InvocationEvaluator invocationEvaluator;
    private final IndexEvaluator indexEvaluator;

    @Override
    public Object executeProgram(List<Expression> expressions) {
        // Populate global environment with native functions
        nativeFunctions.getNativeFunctions().forEach(nativeFunction -> globalEnvironment.define(
                nativeFunction.name(),
                nativeFunction.lambda()));

        Object finalExpressionResult = null;
        try {
            for (Expression expression : expressions) {
                finalExpressionResult = evaluateExpression(expression);
            }
        } catch (RuntimeError error) {
            errorReporter.reportRuntimeError(error);
        }
        return finalExpressionResult;
    }

    @Override
    public Object evaluateExpression(Expression expression) {
        return switch (expression) {
            case Expression.Literal literalExpression -> literalEvaluator
                    .evaluateExpression(this, literalExpression);
            case Expression.Variable variableExpression -> variableEvaluator
                    .evaluateExpression(this, variableExpression);
            case Expression.Declaration declarationExpression -> declarationEvaluator
                    .evaluateExpression(this, declarationExpression);
            case Expression.Assignment assignmentExpression -> assignmentEvaluator
                    .evaluateExpression(this, assignmentExpression);
            case Expression.Group groupExpression -> groupEvaluator
                    .evaluateExpression(this, groupExpression);
            case Expression.Unary unaryExpression -> unaryExecutor
                    .evaluateExpression(this, unaryExpression);
            case Expression.Binary.Operation binaryOperationExpression -> binaryOperationEvaluator
                    .evaluateExpression(this, binaryOperationExpression);
            case Expression.Binary.Logical logicalExpression -> logicalEvaluator
                    .evaluateExpression(this, logicalExpression);
            case Expression.Binary.Infix infixExpression -> infixEvaluator
                    .evaluateExpression(this, infixExpression);
            case Expression.Block blockExpression -> blockEvaluator
                    .evaluateExpression(this, blockExpression);
            case Expression.If ifExpression -> ifEvaluator
                    .evaluateExpression(this, ifExpression);
            case Expression.While whileExpression -> whileEvaluator
                    .evaluateExpression(this, whileExpression);
            case Expression.Invocation invocationExpression -> invocationEvaluator
                    .evaluateExpression(this, invocationExpression);
            case Expression.Lambda lambdaExpression -> lambdaEvaluator
                    .evaluateExpression(this, lambdaExpression);
            case Expression.Index indexExpression -> indexEvaluator
                    .evaluateExpression(this, indexExpression);
            case Expression.ArgumentHole argumentHoleExpression -> argumentHoleExpression;
        };
    }

    @Override
    public void resolveStackVariableAtDepth(Expression expression, int depth) {
        stackDepthMap.put(expression, depth);
    }

    @Override
    public void assignStackVariable(Expression.Assignment expression, Object value) {
        if (stackDepthMap.containsKey(expression)) {
            currentEnvironment.assignAtAncestor(stackDepthMap.get(expression), expression.variable(), value);
        } else {
            globalEnvironment.assign(expression.variable(), value);
        }
    }

    @Override
    public Object lookUpStackVariable(Expression.Variable variable) {
        if (stackDepthMap.containsKey(variable)) {
            return currentEnvironment.getAt(stackDepthMap.get(variable), variable.name().lexeme());
        } else {
            return globalEnvironment.get(variable.name());
        }
    }

    @Override
    public Environment getCurrentScope() {
        return currentEnvironment;
    }

    @Override
    public Object evaluateExpressionInNewScope(Supplier<Object> expressionEvaluator) {
        return evaluateExpressionInGivenScope(expressionEvaluator, new Environment(currentEnvironment));
    }

    @Override
    public Object evaluateExpressionInGivenScope(Supplier<Object> expressionEvaluator, Environment scope) {
        Environment previousEnvironment = currentEnvironment;
        currentEnvironment = scope;
        try {
            return expressionEvaluator.get();
        } finally {
            currentEnvironment = previousEnvironment;
        }
    }

    @Override
    public Object invokeLambda(Invokable invokable, List<Object> arguments, Token token) {
        if (arguments.size() != invokable.arity()) {
            throw new RuntimeError(token, String.format("Expected %d arguments but got %d.", invokable.arity(), arguments.size()));
        }
        return invokable.call(this, arguments);
    }
}
