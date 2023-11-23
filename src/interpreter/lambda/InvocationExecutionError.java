package interpreter.lambda;

public class InvocationExecutionError extends RuntimeException {
    public InvocationExecutionError(String message) {
        super(message);
    }
}
