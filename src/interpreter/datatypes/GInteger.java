package interpreter.datatypes;

public record GInteger(Integer value) implements GNumeric {
    @Override
    public Double toDouble() {
        return (double) value();
    }

    @Override
    public GBoolean greaterThan(GNumeric other) {
        return new GBoolean(toDouble() > other.toDouble());
    }

    @Override
    public GBoolean greaterThanOrEqualTo(GNumeric other) {
        return new GBoolean(toDouble() > other.toDouble());
    }

    @Override
    public GBoolean lessThan(GNumeric other) {
        return null;
    }

    @Override
    public GBoolean lessThanOrEqualTo(GNumeric other) {
        return null;
    }

    @Override
    public GNumeric add(GNumeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() + gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() + gDouble.value());
        };
    }

    @Override
    public GNumeric subtract(GNumeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() - gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() - gDouble.value());
        };
    }

    @Override
    public GNumeric multiply(GNumeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() * gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() * gDouble.value());
        };
    }

    @Override
    public GNumeric divide(GNumeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() / gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() / gDouble.value());
        };
    }
}
