package interpreter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import error.ErrorReporter;
import interpreter.datatypes.GObject;
import interpreter.errors.RuntimeError;
import interpreter.evaluators.*;
import interpreter.lambda.InvocationExecutionError;
import interpreter.lambda.Invokable;
import interpreter.standardlibrary.LibraryStructFactory;
import interpreter.runtime.Environment;
import lombok.RequiredArgsConstructor;
import model.Expression;
import model.SugarExpression;
import model.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Singleton
@RequiredArgsConstructor(onConstructor_ = { @Inject})
public class TreeWalkInterpreter implements Interpreter {
    private final ErrorReporter errorReporter;
    private final LibraryStructFactory libraryStructFactory;
    private final Map<Expression, Integer> stackDepthMap = new HashMap<>();
    private final Environment globalEnvironment = Environment.createGlobalEnvironment();
    private Environment currentEnvironment = globalEnvironment;

    private final LiteralEvaluator literalEvaluator;
    private final ListLiteralEvaluator listLiteralEvaluator;
    private final StructFieldDeclarationEvaluator structFieldDeclarationEvaluator;
    private final StructLiteralEvaluator structLiteralEvaluator;
    private final VariableEvaluator variableEvaluator;
    private final DeclarationEvaluator declarationEvaluator;
    private final AssignmentEvaluator assignmentEvaluator;
    private final IndexAssignmentEvaluator indexAssignmentEvaluator;
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
    public GObject executeProgram(List<Expression> expressions) {
        // Add standard library to the global scope
        globalEnvironment.define(
                libraryStructFactory.getStandardLibraryName(),
                libraryStructFactory.buildStandardLibraryStruct());

        GObject finalExpressionResult = null;
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
    public GObject evaluateExpression(Expression expression) {
        return switch (expression) {
            case Expression.Literal literalExpression -> literalEvaluator
                    .evaluateExpression(this, literalExpression);
            case Expression.ListLiteral listLiteralExpression -> listLiteralEvaluator
                    .evaluateExpression(this, listLiteralExpression);
            case Expression.StructFieldDeclaration fieldDeclarationExpression -> structFieldDeclarationEvaluator
                    .evaluateExpression(this, fieldDeclarationExpression);
            case Expression.StructLiteral structLiteralExpression -> structLiteralEvaluator
                    .evaluateExpression(this, structLiteralExpression);
            case Expression.Variable variableExpression -> variableEvaluator
                    .evaluateExpression(this, variableExpression);
            case Expression.Declaration declarationExpression -> declarationEvaluator
                    .evaluateExpression(this, declarationExpression);
            case Expression.Assignment assignmentExpression -> assignmentEvaluator
                    .evaluateExpression(this, assignmentExpression);
            case Expression.IndexAssignment indexAssignmentExpression -> indexAssignmentEvaluator
                    .evaluateExpression(this, indexAssignmentExpression);
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
            case SugarExpression sugarExpression -> throw new RuntimeError(sugarExpression.getErrorReportingToken(), "A sugar expression was encountered in the interpreter, which caused it to panic. This expression cannot be interpreted, and should have been desugared before interpretation. This is a bug in the GryphonScipt language implementation.");
        };
    }

    @Override
    public void resolveStackVariableAtDepth(Expression expression, int depth) {
        stackDepthMap.put(expression, depth);
    }

    @Override
    public void assignStackVariable(Expression.Assignment expression, GObject value) {
        if (stackDepthMap.containsKey(expression)) {
            currentEnvironment.assignAtAncestor(stackDepthMap.get(expression), expression.variable(), value);
        } else {
            globalEnvironment.assign(expression.variable(), value);
        }
    }

    @Override
    public GObject lookUpStackVariable(Expression.Variable variable) {
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
    public GObject evaluateExpressionInNewScope(Supplier<GObject> expressionEvaluator) {
        return evaluateExpressionInGivenScope(expressionEvaluator, new Environment(currentEnvironment));
    }

    @Override
    public GObject evaluateExpressionInGivenScope(Supplier<GObject> expressionEvaluator, Environment scope) {
        Environment previousEnvironment = currentEnvironment;
        currentEnvironment = scope;
        try {
            return expressionEvaluator.get();
        } finally {
            currentEnvironment = previousEnvironment;
        }
    }

    @Override
    public GObject invokeLambda(Invokable invokable, List<GObject> arguments, Token token) {
        if (arguments.size() != invokable.arity()) {
            throw new RuntimeError(token, String.format("Expected %d arguments but got %d.", invokable.arity(), arguments.size()));
        }
        try {
            return invokable.call(this, arguments);
        } catch (InvocationExecutionError error) {
            throw new RuntimeError(token, error.getMessage());
        }
    }
}
