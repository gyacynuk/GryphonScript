package interpreter.errors;

import lombok.Getter;
import model.Token;

@Getter
public class RuntimeError extends RuntimeException {
    private final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
