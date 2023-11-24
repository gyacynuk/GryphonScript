package model;

public enum TokenType {
    // Single-character tokens
    LEFT_BRACKET, RIGHT_BRACKET, LEFT_CURLY, RIGHT_CURLY, LEFT_SQUARE, RIGHT_SQUARE,
    MINUS, PLUS, SLASH, STAR,
    CONCAT,
    COMMA, DOT, COLON, SEMICOLON, UNDERSCORE, BACK_SLASH,

    // Single and Double character tokens
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    AND, OR,
    INFIX, ARROW,

    // Literals
    IDENTIFIER, STRING, INTEGER, DOUBLE,


    // Keywords
    TRUE, FALSE,
    IF, ELSE, WHILE,
    LET, NIL,

    EOF
}
