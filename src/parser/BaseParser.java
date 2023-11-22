package parser;

import error.ErrorReporter;
import model.Token;
import model.TokenType;
import parser.errors.ParseError;

import java.util.List;

import static model.TokenType.EOF;
import static model.TokenType.SEMICOLON;

public abstract class BaseParser {
    protected ErrorReporter errorReporter;
    protected List<Token> tokens;
    protected int current = 0;    // Eager look-ahead

    BaseParser(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    protected void loadTokens(List<Token> tokens) {
        this.tokens = tokens;
        current = 0;
    }

    protected boolean isAtEnd() {
        return peek().type() == EOF;
    }

    protected Token peek() {
        return tokens.get(current);
    }

    protected Token previous() {
        return tokens.get(current - 1);
    }

    protected Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    protected boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    protected boolean matchAny(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return true;
            }
        }

        return false;
    }

    protected boolean matchAndConsumeAny(TokenType... types) {
        if (matchAny(types)) {
            advance();
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the next token is of the expected type. If so, it consumes the token and everything is groovy.
     * If some other token is there, then we’ve hit an error.
     */
    protected Token consume(TokenType type, String errorMessage) {
        if (check(type)) return advance();

        throw error(peek(), errorMessage);
    }

    protected ParseError error(Token token, String message) {
        errorReporter.reportErrorAtToken(token, message);
        return new ParseError(message);
    }

    protected void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type() == SEMICOLON) return;

            switch (peek().type()) {
                case LET, IF, WHILE -> {
                    return;
                }
                default -> advance();
            }
        }
    }
}
