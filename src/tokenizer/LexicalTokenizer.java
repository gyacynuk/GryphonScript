package tokenizer;

import com.google.inject.Inject;
import error.ErrorReporter;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import model.Token;
import model.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static model.TokenType.*;
import static model.TokenType.INFIX;

@Singleton
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class LexicalTokenizer implements Tokenizer {
    private static final char NULL_CHAR = '\0';
    private static final char QUOTE_CHAR = '\'';

    private final KeywordManager keywordManager;
    private final ErrorReporter errorReporter;

    private int start = 0;
    private int current = 0;
    private int lineNumber = 1;
    private String source;


    public List<Token> tokenize(String source) {
        loadSource(source);

        List<Token> tokens = new ArrayList<>();
        while(!isAtEndOfFile()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken().ifPresent(tokens::add);
        }

        tokens.add(new Token(EOF, "", null, lineNumber));
        return tokens;
    }

    private void loadSource(String source) {
        this.source = source;
        start = 0;
        current = 0;
        lineNumber = 1;
    }

    private boolean isAtEndOfFile() {
        return current >= source.length();
    }

    private char getCharSafely(int index) {
        return index >= source.length() ? NULL_CHAR : source.charAt(index);
    }

    private char advanceAndGetCurrent() {
        char currentChar = getCharSafely(current);
        current++;
        return currentChar;
    }

    private char peek() {
        return getCharSafely(current);
    }

    private char peekNext() {
        return getCharSafely(current + 1);
    }

    private boolean isDigitStrict(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isDigit(char c) {
        return isDigitStrict(c) || c == '_';
    }

    private boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z' ||
                c >= 'A' && c <= 'Z' ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isDigitStrict(c) || isAlpha(c);
    }

    private boolean matchAndAdvance(char expected) {
        if (isAtEndOfFile() || getCharSafely(current) != expected) {
            return false;
        }

        current ++;
        return true;
    }

    private TokenType conditionalMatchAndAdvance(char expected, TokenType typeIfMatched, TokenType typeIfNotMatched) {
        return matchAndAdvance(expected) ? typeIfMatched : typeIfNotMatched;
    }

    private Optional<Token> createToken(TokenType type) {
        return createToken(type, null);
    }

    private Optional<Token> createToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        return Optional.of(new Token(type, text, literal, lineNumber));
    }

    /**
     * Add the given TokenType iff the given predicate evaluates to true.
     *
     * @param predicate predicate function to determine if the token should be added, based on current Lexer state.
     * @param typeIfMatched TokenType to add if predicate evaluates to true.
     */
    private Optional<Token> createTokenIfMatch(Supplier<Boolean> predicate, TokenType typeIfMatched) {
        if (predicate.get()) {
            return createToken(typeIfMatched);
        }
        return Optional.empty();
    }

    private Optional<Token> createTokenForFirstMatch(List<Supplier<Optional<Token>>> optionalSuppliers) {
        return optionalSuppliers.stream()
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private Optional<Token> scanToken() {
        char c = advanceAndGetCurrent();

        return switch (c) {
            // Match single-character tokens
            case '(' -> createToken(LEFT_BRACKET);
            case ')' -> createToken(RIGHT_BRACKET);
            case '{' -> createToken(LEFT_CURLY);
            case '}' -> createToken(RIGHT_CURLY);
            case '[' -> createToken(LEFT_SQUARE);
            case ']' -> createToken(RIGHT_SQUARE);
            case '.' -> createToken(DOT);
            case ',' -> createToken(COMMA);
            case '+' -> createToken(PLUS);
            case '*' -> createToken(STAR);
            case '@' -> createToken(CONCAT);
            case ';' -> createToken(SEMICOLON);
            case '\\' -> createToken(BACK_SLASH);

            // Match with one character look-ahead to find potential double character lexemes
            case '!' -> createToken(conditionalMatchAndAdvance('=', BANG_EQUAL, BANG));
            case '=' -> createToken(conditionalMatchAndAdvance('=', EQUAL_EQUAL, EQUAL));
            case '<' -> createToken(conditionalMatchAndAdvance('=', LESS_EQUAL, LESS));
            case '>' -> createToken(conditionalMatchAndAdvance('=', GREATER_EQUAL, GREATER));
            case '-' -> createToken(conditionalMatchAndAdvance('>', ARROW, MINUS));

            // Match strictly 2 character tokens without valid prefixes
            // TODO: log error with no match (wrapper function for createTokenForFirstMatch() which logs when empty)
            case '&' -> createTokenIfMatch(() -> matchAndAdvance('&'), AND);
            case '|' -> createTokenForFirstMatch(Arrays.asList(
                    () -> createTokenIfMatch(() -> matchAndAdvance('|'), OR),
                    () -> createTokenIfMatch(() -> matchAndAdvance('>'), INFIX)));

            // Ignore whitespace
            case ' ', '\r', '\t' -> Optional.empty();

            // Match new lines
            case '\n' -> {
                lineNumber++;
                yield Optional.empty();
            }

            // Match underscores, either stand-alone (_), or as identifiers (_private)
            case '_' -> consumeLeadingUnderscore();

            // Match comments (// ...) or slashes (/)
            case '/' -> consumeComment();

            // Match string literals
            case QUOTE_CHAR -> consumeString();

            default -> {
                // Match identifiers
                if (isAlpha(c)) yield consumeIdentifier();
                else if (isDigitStrict(c)) yield consumeNumber();
                // Report errors for unexpected characters
                else {
                    errorReporter.reportErrorAtLine(lineNumber,"Unexpected character: " + getCharSafely(current));
                    yield Optional.empty();
                }
            }
        };
    }

    private Optional<Token> consumeLeadingUnderscore() {
        if (isDigitStrict(peek())) {
            errorReporter.reportErrorAtLine(lineNumber,"Numbers cannot be prefixed with '_'");
            return Optional.empty();
        } else if (isAlpha(peek())) {
            return consumeIdentifier();
        } else {
            return createToken(UNDERSCORE);
        }
    }

    private Optional<Token> consumeComment() {
        if (matchAndAdvance('/')) {
            // A comment continues until it reached the end of line. Read the input but discard it.
            while (peek() != '\n' && !isAtEndOfFile()) advanceAndGetCurrent();
            return Optional.empty();
        } else {
            return createToken(SLASH);
        }
    }

    private Optional<Token> consumeString() {
        while (peek() != QUOTE_CHAR && !isAtEndOfFile()) {
            if (peek() == '\n') lineNumber ++;
            advanceAndGetCurrent();
        }

        if (isAtEndOfFile()) {
            errorReporter.reportErrorAtLine(lineNumber, "Unterminated string");
            return Optional.empty();
        }

        // Consume the closing '
        advanceAndGetCurrent();

        // Trim the surrounding ''
        String value = source.substring(start+1, current-1);
        return createToken(STRING, value);
    }

    private Optional<Token> consumeNumber() {
        while (isDigit(peek())) advanceAndGetCurrent();

        // Look for decimal point
        boolean isDouble = false;
        if (peek() == '.' && isDigit(peekNext())) {
            advanceAndGetCurrent(); // consume the decimal point
            isDouble = true;
            while (isDigit(peek())) advanceAndGetCurrent();
        }

        String numberWithUnderscores = source.substring(start, current);
        String cleanedNumber = numberWithUnderscores.replaceAll("_", "");

        return isDouble
                ? createToken(DOUBLE, Double.parseDouble(cleanedNumber))
                : createToken(INTEGER, Integer.parseInt(cleanedNumber));
    }

    private Optional<Token> consumeIdentifier() {
        while (isAlphaNumeric(peek())) advanceAndGetCurrent();

        String value = source.substring(start, current);
        TokenType type = keywordManager.getKeywordOrIdentifier(value);
        return createToken(type);
    }
}
