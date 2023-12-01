package interpreter.datatypes;

public record GInteger(Integer value) implements GNumeric {
    public static final String TYPE_NAME = "integer";

    @Override
    public String typeName() {
        return TYPE_NAME;
    }

    @Override
    public Double toDouble() {
        return (double) value();
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

    @Override
    public GNumeric modulo(GNumeric other) {
        return switch (other) {
            case GInteger gInteger -> new GInteger(value() % gInteger.value());
            case GDouble gDouble -> new GDouble(toDouble() % gDouble.value());
        };
    }
}
