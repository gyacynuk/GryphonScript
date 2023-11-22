package model;

public record Token (TokenType type, String lexeme, Object literal, int line) {
    @Override
    public String toString() {
        return String.format("Token(type:%s, lexeme:%s, literal:%s)", type, lexeme, literal);
    }
}