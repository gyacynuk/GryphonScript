package error;

import com.google.inject.Singleton;
import interpreter.errors.RuntimeError;
import model.Token;
import model.TokenType;
import lombok.Getter;

@Getter
@Singleton
public class ErrorReporter {
    private static final String ERROR_TEMPLATE = "[line %d] Error at %s: %s\n";
    private static final String RUNTIME_ERROR_TEMPLATE = "[line %d] %s\n";

    private boolean isInError = false;
    private boolean isInRuntimeError = false;

    public void clearErrors() {
        isInError = false;
        isInRuntimeError = false;
    }

    public void reportErrorAtLine(int lineNum, String message) {
        report(lineNum, "", message);
    }

    public void reportErrorAtToken(Token token, String message) {
        if (token.type() == TokenType.EOF) {
            report(token.line(), "end", message);
        } else {
            report(token.line(), "'" + token.lexeme() + "'", message);
        }
    }

    public void reportRuntimeError(RuntimeError error) {
        isInRuntimeError = true;
        System.err.printf(RUNTIME_ERROR_TEMPLATE, error.getToken().line(), error.getMessage());
    }

    private void report(int lineNum, String where, String message) {
        if (!where.isBlank()) {
            where = " " + where;
        }

        isInError = true;
        System.err.printf(ERROR_TEMPLATE, lineNum, where, message);
    }
}
