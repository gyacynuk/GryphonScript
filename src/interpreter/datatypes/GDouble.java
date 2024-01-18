package interpreter.datatypes;

public record GDouble(Double value) implements GNumeric {
    public static final String TYPE_NAME = "double";

    @Override
    public String typeName() {
        return TYPE_NAME;
    }

    @Override
    public Double toDouble() {
        return value;
    }

    @Override
    public GNumeric add(GNumeric other) {
        return new GDouble(value() + other.toDouble());
    }

    @Override
    public GNumeric subtract(GNumeric other) {
        return new GDouble(value() - other.toDouble());
    }

    @Override
    public GNumeric multiply(GNumeric other) {
        return new GDouble(value() * other.toDouble());
    }

    @Override
    public GNumeric divide(GNumeric other) {
        return new GDouble(value() / other.toDouble());
    }

    @Override
    public GNumeric modulo(GNumeric other) {
        return new GDouble(value() / other.toDouble());
    }

    @Override
    public GNumeric power(GNumeric other) {
        return new GDouble(Math.pow(value(), other.toDouble()));
    }
}
