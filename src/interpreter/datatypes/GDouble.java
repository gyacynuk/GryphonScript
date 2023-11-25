package interpreter.datatypes;

public record GDouble(Double value) implements GNumeric {
    @Override
    public String typeName() {
        return "double";
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
}
