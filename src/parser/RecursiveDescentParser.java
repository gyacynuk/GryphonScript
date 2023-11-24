package parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import error.ErrorReporter;
import interpreter.datatypes.GBoolean;
import interpreter.datatypes.GDouble;
import interpreter.datatypes.GInteger;
import interpreter.datatypes.GString;

import model.*;
import parser.errors.ParseError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static model.TokenType.*;

@Singleton
public class RecursiveDescentParser extends BaseParser implements Parser {
    @Inject
    public RecursiveDescentParser(ErrorReporter errorReporter) {
        super(errorReporter);
    }
    public List<Expression> parse(List<Token> tokens) {
        super.loadTokens(tokens);

        List<Expression> expressions = new ArrayList<>();
        while (!isAtEnd()) {
            expressions.add(parseExpression());
        }

        return expressions;
    }

    private Expression parseExpression() {
        try {
            if (matchAndConsumeAny(LET)) return parseDeclaration();
            else return parseExpressionStatement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Expression parseDeclaration() {
        // Parse destructure declaration
        if (matchAny(LEFT_SQUARE, LEFT_CURLY)) {
            SugarExpression.Destructure destructure = parseDestructure(Collections.emptyList());
            Token equals = consume(EQUAL, "Expected '=' after destructure declaration and before definition");
            Expression initializer = parseExpressionStatement(); // Must be an expression (not another declaration) following a destructuring
            return new SugarExpression.DestructureDeclaration(destructure, equals, initializer);
        }
        // Parse variable declaration
        else {
            Token name = consume(IDENTIFIER, "Expected variable name");

            Expression initializer = null;
            if (matchAndConsumeAny(EQUAL)) {
                initializer = parseExpression();
            }

            return new Expression.Declaration(name, initializer);
        }
    }

    private SugarExpression.Destructure parseDestructure(List<Expression> context) {
        if (matchAny(LEFT_SQUARE, LEFT_CURLY)) {
            return matchAny(LEFT_SQUARE)
                    ? parseArrayDestructure(context)
                    : parseStructDestructure(context);
        }
        throw error(peek(), String.format("Expected destructuring declaration but found: %s", peek().lexeme()));
    }

    private SugarExpression.Destructure.ArrayDestructure parseArrayDestructure(List<Expression> context) {
        Token openingBracket = consume(LEFT_SQUARE, "Expected array destructuring to begin with '['");
        if (check(RIGHT_SQUARE)) {
            throw error(peek(), "Empty array destructure declaration is not allowed");
        }

        List<SugarExpression.ArrayDestructureField> fields = new ArrayList<>();
        int runningCount = 0;
        do {
            Expression indexContext = new Expression.Literal(new GInteger(runningCount++));
            List<Expression> newContext = new ArrayList<>(context);
            newContext.add(indexContext);

            if (matchAny(IDENTIFIER)) {
                Token fieldName = consume(IDENTIFIER, "Expected identifier as field name in array destructure");
                fields.add(new SugarExpression.ArrayDestructureField.FieldDeclaration(fieldName, newContext, null));
            }
            else {
                fields.add(new SugarExpression.ArrayDestructureField.FieldDeclaration(null, newContext, parseDestructure(newContext)));
            }
        } while (matchAndConsumeAny(COMMA));

        Token closingBracket = consume(RIGHT_SQUARE, "Expected array destructuring to end with ']'");
        return new SugarExpression.Destructure.ArrayDestructure(fields, closingBracket);
    }

    private SugarExpression.Destructure.StructDestructure parseStructDestructure(List<Expression> context) {
        consume(LEFT_CURLY, "Expected struct destructuring to begin with '{'");
        if (check(RIGHT_CURLY)) {
            throw error(peek(), "Empty struct destructure declaration is not allowed");
        }

        List<SugarExpression.StructDestructureField> fields = new ArrayList<>();
        do {
            Token fieldName = consume(IDENTIFIER, "Expected identifier for struct destructure field name");
            List<Expression> newContext = new ArrayList<>(context);
            newContext.add(new Expression.Literal(new GString(fieldName.lexeme())));
            SugarExpression.Destructure nullableFieldValue = null;

            // Allow for variable punning by optionally recursing on nested field
            if (matchAny(COLON)) {
                consume(COLON, "Expected colon after struct destructure field name");
                nullableFieldValue = parseDestructure(newContext);
            }

            fields.add(new SugarExpression.StructDestructureField.FieldDeclaration(fieldName, newContext, nullableFieldValue));
        } while (matchAndConsumeAny(COMMA));

        Token closingBracket = consume(RIGHT_CURLY, "Expected struct destructuring to end with '}'");
        return new SugarExpression.Destructure.StructDestructure(fields, closingBracket);
    }

    /**
     * Allows for block-based expressions to exist as expression statements without the need for a trailing ;, however
     * this is enforced for non-block-based expressions.
     */
    private Expression parseExpressionStatement() {
        return parseInfixExpression();
    }


    private Expression parseBlockOrExpressionStatement() {
        return matchAndConsumeAny(LEFT_CURLY) ? parseBlockExpression() : parseExpressionStatement();
    }

    private Expression parseBlockExpression() {
        List<Expression> blockExpressions = new ArrayList<>();

        while (!check(RIGHT_CURLY) && !isAtEnd()) {
            blockExpressions.add(parseExpression());
        }

        consume(RIGHT_CURLY, "Expected '}' after block");
        return new Expression.Block(blockExpressions);
    }

    private Expression parseIfExpression() {
        consume(LEFT_BRACKET, "Expected '(' after 'if'");
        Expression condition = parseExpressionStatement();
        consume(RIGHT_BRACKET, "Expected ')' after if condition");

        // Not required to be an expression statement iff followed by an 'else' branch, to make for a cleaner ternary
        // operator
        Expression thenBranch = parseBlockOrExpressionStatement();
        Expression elseBranch = null;

        // eagerly looks for an else before returning
        if (matchAndConsumeAny(ELSE)) {
            elseBranch = parseBlockOrExpressionStatement();
        }

        return new Expression.If(condition, thenBranch, elseBranch);
    }

    private Expression parseWhileExpression() {
        consume(LEFT_BRACKET, "Expected '(' after 'while'");
        Expression condition = parseExpressionStatement();
        consume(RIGHT_BRACKET, "Expected ')' after while condition");

        Expression body = parseBlockOrExpressionStatement();

        return new Expression.While(condition, body);
    }

    private Expression parseLambdaExpression() {
        consume(LEFT_BRACKET, "Expected '(' after '\\' during lambda declaration");

        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_BRACKET)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Lambda cannot have more than 255 parameters");
                }
                parameters.add(consume(IDENTIFIER, "Expected parameter name"));
            } while (matchAndConsumeAny(COMMA));
        }

        consume(RIGHT_BRACKET, "Expected ')' after lambda parameters");
        consume(ARROW, "Expected '->' before lambda body");
        Expression body = parseBlockOrExpressionStatement();

        return new Expression.Lambda(parameters, body);
    }

    private Expression parseInfixExpression() {
        return parseAssignmentExpression();
    }

    private Expression parseAssignmentExpression() {
        Expression lValueOrExpr = parseInfixFunctionApplicationExpression();

        if (matchAndConsumeAny(EQUAL)) {
            Token equals = previous();
            Expression rValue = parseAssignmentExpression();

            // lValueOrExpr is actually and r-value at this point in time, but below we convert it to a true l-value
            if (lValueOrExpr instanceof Expression.Variable variable) {
                return new Expression.Assignment(variable.name(), rValue);
            } else if (lValueOrExpr instanceof Expression.Index index) {
                return new Expression.IndexAssignment(index.callee(), index.index(), rValue, index.closingBracketOrDot());
            }

            throw error(equals, "Invalid assignment target");
        }

        return lValueOrExpr;
    }

    private Expression generateLeftAssociativeBinaryGrammarRuleParser(
            Function<BinaryExpressionInitializer.Args, Expression.Binary> initializer,
            Supplier<Expression> nextHighestPrecedenceParser,
            TokenType... operators) {
        Expression expr = nextHighestPrecedenceParser.get();

        while (matchAndConsumeAny(operators)) {
            Token operator = previous();
            Expression right = nextHighestPrecedenceParser.get();
            expr = initializer.apply(new BinaryExpressionInitializer.Args(expr, right, operator));
        }

        return expr;
    }

    private Expression parseInfixFunctionApplicationExpression() {
        return generateLeftAssociativeBinaryGrammarRuleParser(
                BinaryExpressionInitializer::initInfix,
                this::parseOrExpression,
                INFIX);
    }

    private Expression parseOrExpression() {
        return generateLeftAssociativeBinaryGrammarRuleParser(
                BinaryExpressionInitializer::initLogical,
                this::parseAndExpression,
                OR);
    }

    private Expression parseAndExpression() {
        return generateLeftAssociativeBinaryGrammarRuleParser(
                BinaryExpressionInitializer::initLogical,
                this::parseEqualityExpression,
                AND);
    }

    private Expression parseEqualityExpression() {
        return generateLeftAssociativeBinaryGrammarRuleParser(
                BinaryExpressionInitializer::initOperation,
                this::parseComparisonExpression,
                EQUAL_EQUAL, BANG_EQUAL);
    }

    private Expression parseComparisonExpression() {
        return generateLeftAssociativeBinaryGrammarRuleParser(
                BinaryExpressionInitializer::initOperation,
                this::parseTermExpression,
                GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    private Expression parseTermExpression() {
        return generateLeftAssociativeBinaryGrammarRuleParser(
                BinaryExpressionInitializer::initOperation,
                this::parseFactorExpression,
                MINUS, PLUS, STRING_CONCAT, LIST_CONCAT);
    }

    private Expression parseFactorExpression() {
        return generateLeftAssociativeBinaryGrammarRuleParser(
                BinaryExpressionInitializer::initOperation,
                this::parseUnaryExpression,
                SLASH, STAR);
    }

    private Expression parseUnaryExpression() {
        if (matchAndConsumeAny(BANG, MINUS)) {
            Token operator = previous();
            Expression right = parseUnaryExpression();
            return new Expression.Unary(operator, right);
        }

        return parseInvocationExpression();
    }

    private Expression parseInvocationExpression() {
        Expression expr = parsePrimaryExpression();

        while (matchAny(LEFT_BRACKET, LEFT_SQUARE, DOT)) {
            if (matchAndConsumeAny(LEFT_BRACKET)) expr = finishInvocation(expr);
            else if (matchAndConsumeAny(LEFT_SQUARE)) expr = finishIndexing(expr);
            else if (matchAndConsumeAny(DOT)) expr = finishDotIndexing(expr);
        }

        return expr;
    }

    private Expression finishInvocation(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(RIGHT_BRACKET)) {
            do {
                arguments.add(parseExpressionStatement());
            } while (matchAndConsumeAny(COMMA));
        }

        Token paren = consume(RIGHT_BRACKET, "Expected ')' after function arguments");
        return new Expression.Invocation(callee, paren, arguments);
    }

    private Expression finishIndexing(Expression callee) {
        Expression index = parseExpressionStatement();
        Token paren = consume(RIGHT_SQUARE, "Expected ']' after index");
        return new Expression.Index(callee, paren, index);
    }

    private Expression finishDotIndexing(Expression callee) {
        Token fieldName = consume(IDENTIFIER, "Expected identifier after '.'");
        Expression variableIndex = new Expression.Literal(new GString(fieldName.lexeme()));
        return new Expression.Index(callee, fieldName, variableIndex);
    }

    private Expression.ListLiteral finishListLiteral() {
        List<Expression> elements = new ArrayList<>();
        if (!check(RIGHT_SQUARE)) {
            do {
                elements.add(parseExpressionStatement());
            } while (matchAndConsumeAny(COMMA));
        }

        Token paren = consume(RIGHT_SQUARE, "Expected ']' to terminate list literal");
        return new Expression.ListLiteral(elements, paren);
    }

    private Expression.StructLiteral finishStructLiteral() {
        List<Expression> fields = new ArrayList<>();
        if (!check(RIGHT_CURLY)) {
            do {
                Token fieldName = consume(IDENTIFIER, "Expected identifier for struct literal field name");
                Expression fieldValue;

                // Allow for variable punning
                if (matchAny(COLON)) {
                    consume(COLON, "Expected colon after struct field name, and before field value");
                    fieldValue = parseExpressionStatement();
                } else {
                    // Set value to a variable which references the field name in the current scope
                    fieldValue = new Expression.Variable(fieldName);
                }

                fields.add(new Expression.StructFieldDeclaration(fieldName, fieldValue));
            } while (matchAndConsumeAny(COMMA));
        }

        Token paren = consume(RIGHT_CURLY, "Expected '}' to terminate struct literal");
        return new Expression.StructLiteral(fields, paren);
    }

    private Expression parsePrimaryExpression() {
        // Literals
        if (matchAndConsumeAny(NIL)) return Expression.NIL;
        if (matchAndConsumeAny(TRUE)) return new Expression.Literal(new GBoolean(true));
        if (matchAndConsumeAny(FALSE)) return new Expression.Literal(new GBoolean(false));
        if (matchAndConsumeAny(INTEGER)) return new Expression.Literal(new GInteger((Integer) previous().literal()));
        if (matchAndConsumeAny(DOUBLE)) return new Expression.Literal(new GDouble((Double) previous().literal()));
        if (matchAndConsumeAny(STRING)) return new Expression.Literal(new GString((String) previous().literal()));

        // Identifiers
        if (matchAndConsumeAny(UNDERSCORE)) return Expression.HOLE;
        if (matchAndConsumeAny(IDENTIFIER)) return new Expression.Variable(previous());

        // Groups
        if (matchAndConsumeAny(LEFT_BRACKET)) {
            Expression expression = parseExpressionStatement();
            consume(RIGHT_BRACKET, "Expected ')' after expression.");
            return new Expression.Group(expression);
        }
        // Lists
        if (matchAndConsumeAny(LEFT_SQUARE)) return finishListLiteral();
        // Structs
        if (matchAndConsumeAny(LEFT_CURLY)) return finishStructLiteral();

        // Control keywords
        if (matchAndConsumeAny(IF)) return parseIfExpression();
        if (matchAndConsumeAny(WHILE)) return parseWhileExpression();

        // Lambdas
        if (matchAndConsumeAny(BACK_SLASH)) return parseLambdaExpression();

        throw error(peek(), "Expected expression.");
    }
}
