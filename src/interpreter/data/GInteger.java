package interpreter.data;

public record GInteger(Integer value) implements GObject.Numeric {
    @Override
    public Double toDouble() {
        return (double) value();
    }

    @Override
    public GBoolean greaterThan(Numeric other) {
        return new GBoolean(toDouble() > other.toDouble());
    }

    @Override
    public GBoolean greaterThanOrEqualTo(Numeric other) {
        return new GBoolean(toDouble() > other.toDouble());
    }

    @Override
    public GBoolean lessThan(Numeric other) {
        return null;
    }

    @Override
    public GBoolean lessThanOrEqualTo(Numeric other) {
        return null;
    }

    @Override
    public Numeric add(Numeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() + gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() + gDouble.value());
        };
    }

    @Override
    public Numeric subtract(Numeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() - gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() - gDouble.value());
        };
    }

    @Override
    public Numeric multiply(Numeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() * gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() * gDouble.value());
        };
    }

    @Override
    public Numeric divide(Numeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() / gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() / gDouble.value());
        };
    }
}
