package model;

public record Token(TokenType type, String lexeme, Object literal, int line, boolean isOnNewLine) implements SugarExpression.DestructureLambdaParam {
    @Override
    public String toString() {
        return String.format("Token(type:%s, lexeme:%s, literal:%s)", type, lexeme, literal);
    }
}