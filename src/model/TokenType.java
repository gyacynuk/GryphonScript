package model;

public enum TokenType {
    // Single-character tokens
    LEFT_BRACKET, RIGHT_BRACKET, LEFT_CURLY, RIGHT_CURLY, LEFT_SQUARE, RIGHT_SQUARE,
    MINUS, PLUS, SLASH, STAR,
    COMMA, DOT, SEMICOLON, UNDERSCORE, BACK_SLASH,

    // Single and Double character tokens
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    AND, OR,
    INFIX, ARROW,

    // Literals
    IDENTIFIER, STRING, NUMBER, // TODO: differentiate between Integers and Decimals (use INTEGERS and doubles, since I want to use ints for array indexing)


    // Keywords
    TRUE, FALSE,
    IF, ELSE, WHILE,
    LET, NIL,

    EOF
}
