package interpreter.datatypes;

import interpreter.lambda.Invokable;

public record GLambda(Invokable value) implements GObject {
    public static final String TYPE_NAME = "lambda";

    @Override
    public String typeName() {
        return TYPE_NAME;
    }

    @Override
    public String stringify() {
        return "<lambda-arity-%d>".formatted(value.arity());
    }
}
