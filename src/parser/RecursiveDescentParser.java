package parser;

import error.ErrorReporter;
import interpreter.data.GBoolean;
import interpreter.data.GDouble;
import interpreter.data.GInteger;
import interpreter.data.GString;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import model.BinaryExpressionInitializer;
import model.Expression;
import model.Token;
import model.TokenType;
import parser.errors.ParseError;

import java.util.ArrayList;
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
        // TODO: match on '{' or '[' here for object/array destructuring. Destructuring should be an expression which
        //  returns the sub-object or sub-array. For instance "let sub = let [a, b] = [1, 2, 3]" should be a valid
        //  expression, where sub == [1, 2]
        Token name = consume(IDENTIFIER, "Expected name name");

        Expression initializer = null;
        if (matchAndConsumeAny(EQUAL)) {
            initializer = parseExpression();
        }

        return new Expression.Declaration(name, initializer);
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
                MINUS, PLUS, CONCAT);
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

        while (matchAny(LEFT_BRACKET, LEFT_SQUARE)) {
            if (matchAndConsumeAny(LEFT_BRACKET)) expr = finishInvocation(expr);
            else if (matchAndConsumeAny(LEFT_SQUARE)) expr = finishIndexing(expr);
        }

        return expr;
    }

    private Expression finishInvocation(Expression callee) {
        List<Expression> arguments = new ArrayList<>();
        if (!check(RIGHT_BRACKET)) {
            do {
                if (arguments.size() >= 255)
                    // Do not throw and panic, just report the error
                    error(peek(), "Function cannot have more than 255 arguments");
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

        // TODO: implement array literals in the same style below:
        // Groups
        if (matchAndConsumeAny(LEFT_BRACKET)) {
            Expression expr = parseExpressionStatement();
            consume(RIGHT_BRACKET, "Expected ')' after expression.");
            return new Expression.Group(expr);
        }

        // Control keywords
        if (matchAndConsumeAny(IF)) return parseIfExpression();
        if (matchAndConsumeAny(WHILE)) return parseWhileExpression();

        // Lambdas
        if (matchAndConsumeAny(BACK_SLASH)) return parseLambdaExpression();

        throw error(peek(), "Expected expression.");
    }
}
