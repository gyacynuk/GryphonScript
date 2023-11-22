package interpreter.data;

public record GDouble(Double value) implements GObject.Numeric {

    @Override
    public Double toDouble() {
        return value;
    }

    @Override
    public Numeric add(Numeric other) {
        return new GDouble(value() + other.toDouble());
    }

    @Override
    public Numeric subtract(Numeric other) {
        return new GDouble(value() - other.toDouble());
    }

    @Override
    public Numeric multiply(Numeric other) {
        return new GDouble(value() * other.toDouble());
    }

    @Override
    public Numeric divide(Numeric other) {
        return new GDouble(value() / other.toDouble());
    }
}
