package desugarer;

import com.google.inject.Singleton;
import interpreter.errors.RuntimeError;
import lombok.NoArgsConstructor;
import model.Expression;
import model.SugarExpression;
import model.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static model.TokenType.IDENTIFIER;

@Singleton
@NoArgsConstructor
public class ExpansionDesugarer extends BaseDesugarer {
    private static int counter = 0;
    private static final String HIDDEN_DESTRUCTURE_VARIABLE_PREFIX = "hidden-destruct-";

    @Override
    public List<Expression> desugarAll(List<Expression> expressions) {
        return expressions.stream()
                // Expand top-level expressions
                .flatMap(expression -> desugarAndExpandTopLevelExpression(expression).stream())
                // Recurse to expand nested blocks
                .map(super::desugarExpression)
                .toList();
    }

    @Override
    protected Expression desugarSugarExpression(SugarExpression sugarExpression) {
        if (sugarExpression instanceof SugarExpression.DestructureLambda destructureLambda) {
            return desugarAndExpandLambda(destructureLambda);
        } else {
            throw new RuntimeError(
                    sugarExpression.getErrorReportingToken(),
                    "An unrecognized nested sugar expression was encountered in the ExpansionDesugarer, which caused it to panic. This is a bug in the GryphonScript language implementation.");
        }
    }

    private List<Expression> desugarAndExpandTopLevelExpression(Expression expression) {
        return switch (expression) {
            case SugarExpression.DestructureDeclaration destructureDeclaration -> desugarAndExpandDestructure(destructureDeclaration);
            case SugarExpression.DestructureLambda destructureLambda -> Collections.singletonList(desugarAndExpandLambda(destructureLambda));
            case SugarExpression sugarExpression -> throw new RuntimeError(sugarExpression.getErrorReportingToken(), "Non-top-level SugarExpression should not be expanded. This is a bug in the GyrphonScript language implementation.");
            default -> Collections.singletonList(expression);
        };
    }

    private List<Expression> desugarAndExpandDestructure(SugarExpression.DestructureDeclaration destructureDeclaration) {
        List<Expression> expandedExpressions = new ArrayList<>();

        // Declare the list/struct to be destructured using a hidden variable name to ensure it does not pollute the
        // developer's environment
        Token targetVariableToken = generateUniqueHiddenToken(destructureDeclaration);
        expandedExpressions.add(new Expression.Declaration(targetVariableToken, destructureDeclaration.initializer()));

        // Create declaration expressions for each destructured field
        expandedExpressions.addAll(
                generateLeafDeclarations(
                        destructureDeclaration.destructureExpression().fields(),
                        targetVariableToken));

        return expandedExpressions;
    }

    private Expression desugarAndExpandLambda(SugarExpression.DestructureLambda destructureLambda) {
        List<Token> lambdaParameters = new ArrayList<>();
        List<Expression> expandedExpressions = new ArrayList<>();

        for (SugarExpression.DestructureLambdaParam sugarParam : destructureLambda.parameters()) {
            switch (sugarParam) {
                case Token token -> lambdaParameters.add(token);
                case SugarExpression.Destructure destructure -> {
                    Token targetParameterToken = generateUniqueHiddenToken(destructureLambda);
                    lambdaParameters.add(targetParameterToken);
                    expandedExpressions.addAll(
                            generateLeafDeclarations(
                                    destructure.fields(),
                                    targetParameterToken));
                }
            }
        }

        Expression desugaredBody = desugarExpression(destructureLambda.body());
        if (desugaredBody instanceof Expression.Block blockBody) {
            expandedExpressions.addAll(blockBody.expressions());
        } else {
            expandedExpressions.add(destructureLambda.body());
        }

        return new Expression.Lambda(
                lambdaParameters,
                new Expression.Block(expandedExpressions),
                false);
    }

    private List<Expression.Declaration> generateLeafDeclarations(List<? extends SugarExpression> fields, Token targetVariableToken) {
        List<Expression.Declaration> declarations = new ArrayList<>();
        fields.forEach(field -> {
            if (field instanceof SugarExpression.ArrayDestructureField arrayDestructureField) {
                // Base case
                if (arrayDestructureField.nullableVariable() != null) {
                    declarations.add(new Expression.Declaration(
                            arrayDestructureField.nullableVariable(),
                            generateIndexExpressionFromContext(arrayDestructureField.context(), targetVariableToken)));
                }
                // Recursive nested case
                else {
                    declarations.addAll(generateLeafDeclarations(
                            arrayDestructureField.nullableNestedDestructure().fields(),
                            targetVariableToken));
                }
            }
            else if (field instanceof SugarExpression.StructDestructureField structDestructureField) {
                // Base case
                if (structDestructureField.nullableNestedDestructure() == null) {
                    declarations.add(new Expression.Declaration(
                            structDestructureField.variable(),
                            generateIndexExpressionFromContext(structDestructureField.context(), targetVariableToken)));
                }
                // Recursive nested case
                else {
                    declarations.addAll(generateLeafDeclarations(
                            structDestructureField.nullableNestedDestructure().fields(),
                            targetVariableToken));
                }
            }
            else {
                throw new RuntimeError(field.getErrorReportingToken(), "Unknown field type encountered during desugaring. This is a bug in the GyrphonScript language implementation.");
            }
        });
        return declarations;
    }

    private Expression generateIndexExpressionFromContext(List<Expression> context, Token targetVariableToken) {
        Expression callee = new Expression.Variable(targetVariableToken);

        for (Expression contextExpression : context) {
            callee = new Expression.Index(callee, targetVariableToken, contextExpression);
        }

        return callee;
    }

    private Token generateUniqueHiddenToken(SugarExpression sugarExpression) {
        Token associatedSourceToken = sugarExpression.getErrorReportingToken();
        String lexeme = HIDDEN_DESTRUCTURE_VARIABLE_PREFIX + counter++;
        return new Token(IDENTIFIER, lexeme, null, associatedSourceToken.line(), false);
    }
}
