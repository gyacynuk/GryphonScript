package resolver;

import error.ErrorReporter;
import interpreter.Interpreter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import model.Expression;
import model.Token;

import java.util.*;

import static resolver.VariableInitializationState.DECLARED;
import static resolver.VariableInitializationState.DEFINED;

@Singleton
@RequiredArgsConstructor(onConstructor_ = { @Inject})
public class SemanticVariableResolver implements Resolver {
    private final ErrorReporter errorReporter;
    private Interpreter interpreter;
    private final Stack<Map<String, VariableInitializationState>> callStackVariableState = new Stack<>();

    @Override
    public void resolveProgram(Interpreter interpreter, List<Expression> expressions) {
        loadInterpreter(interpreter);
        expressions.forEach(this::resolveExpression);
    }

    private void loadInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private void resolveExpression(Expression expression) {
        switch (expression) {
            case Expression.Literal ignored -> { /* do nothing */ }
            case Expression.Assignment assignment -> {
                resolveExpression(assignment.value());
                resolveCallStackScope(assignment, assignment.variable());
            }
            case Expression.Declaration declaration -> {
                declare(declaration.variable());
                if (declaration.initializer() != null) resolveExpression(declaration.initializer());
                define(declaration.variable());
            }
            case Expression.Variable variable -> {
                // Prevent referencing undefined variables
                var variableState = callStackVariableState.isEmpty()
                        ? null
                        : callStackVariableState.peek().get(variable.name().lexeme());
                if (Objects.equals(variableState, DECLARED)) {
                    errorReporter.reportErrorAtToken(variable.name(), "Cannot reference local variable in its own initializer.");
                }
                resolveCallStackScope(variable, variable.name());
            }
            case Expression.Group group -> resolveExpression(group.expression());
            case Expression.Unary unary -> resolveExpression(unary.right());
            case Expression.Binary binary -> {
                resolveExpression(binary.left());
                resolveExpression(binary.right());
            }
            case Expression.Block block -> {
                beginScope();
                block.expressions().forEach(this::resolveExpression);
                endScope();
            }
            case Expression.If ifExpression -> {
                resolveExpression(ifExpression.condition());
                resolveExpression(ifExpression.thenBranch());
                if (ifExpression.elseBranch() != null) resolveExpression(ifExpression.elseBranch());
            }
            case Expression.While whileExpression -> {
                resolveExpression(whileExpression.condition());
                resolveExpression(whileExpression.body());
            }
            case Expression.Invocation invocation -> {
                resolveExpression(invocation.callee());
                invocation.arguments().forEach(this::resolveExpression);
            }
            case Expression.Lambda lambda -> {
                beginScope();
                lambda.parameters().forEach(parameter -> {
                    declare(parameter);
                    define(parameter);
                });
                resolveExpression(lambda.body());
                endScope();
            }
            case Expression.Index index -> {
                resolveExpression(index.callee());
                resolveExpression(index.index());
            }
        }
    }

    private void beginScope() {
        callStackVariableState.push(new HashMap<>());
    }

    private void endScope() {
        callStackVariableState.pop();
    }

    private void declare(Token name) {
        if (callStackVariableState.isEmpty()) return;

        // Prevents re-declaration of a variable in a non-global scope:
        var scope = callStackVariableState.peek();
        if (scope.containsKey(name.lexeme())) {
            errorReporter.reportErrorAtToken(name, "A variable with this name already is declared in this scope");
        }

        scope.put(name.lexeme(), DECLARED);
    }

    private void define(Token name) {
        if (callStackVariableState.isEmpty()) return;

        var scope = callStackVariableState.peek();
        scope.put(name.lexeme(), DEFINED);
    }

    private void resolveCallStackScope(Expression expression, Token variableName) {
        for (int i = callStackVariableState.size() - 1; i >= 0; i--) {
            if (callStackVariableState.get(i).containsKey(variableName.lexeme())) {
                var reverseIndex = callStackVariableState.size() - 1 - i;
                interpreter.resolveStackVariableAtDepth(expression, reverseIndex);
                return;
            }
        }
    }
}
