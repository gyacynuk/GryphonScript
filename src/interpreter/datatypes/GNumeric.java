package interpreter.datatypes;

public sealed interface GNumeric extends GObject permits GInteger, GDouble {
    Double toDouble();
    GNumeric add(GNumeric other);
    GNumeric subtract(GNumeric other);
    GNumeric multiply(GNumeric other);
    GNumeric divide(GNumeric other);
    default GBoolean greaterThan(GNumeric other) {
        return new GBoolean(toDouble() > other.toDouble());
    }
    default GBoolean greaterThanOrEqualTo(GNumeric other) {
        return new GBoolean(toDouble() >= other.toDouble());
    }
    default GBoolean lessThan(GNumeric other) {
        return new GBoolean(toDouble() < other.toDouble());
    }
    default GBoolean lessThanOrEqualTo(GNumeric other) {
        return new GBoolean(toDouble() <= other.toDouble());
    }
}
