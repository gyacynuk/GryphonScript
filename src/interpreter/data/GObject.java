package interpreter.data;

import java.util.List;

public sealed interface GObject permits GHole, GNil, GBoolean, GString, GLambda, GObject.Numeric, GObject.Heap {
    Object value();
    default String stringify() {
        return value().toString();
    }
    sealed interface Numeric extends GObject permits GInteger, GDouble {
        Double toDouble();
        Numeric add(Numeric other);
        Numeric subtract(Numeric other);
        Numeric multiply(Numeric other);
        Numeric divide(Numeric other);
        default GBoolean greaterThan(Numeric other) {
            return new GBoolean(toDouble() > other.toDouble());
        }
        default GBoolean greaterThanOrEqualTo(Numeric other) {
            return new GBoolean(toDouble() >= other.toDouble());
        }
        default GBoolean lessThan(Numeric other) {
            return new GBoolean(toDouble() < other.toDouble());
        }
        default GBoolean lessThanOrEqualTo(Numeric other) {
            return new GBoolean(toDouble() <= other.toDouble());
        }
    }
    sealed interface Heap extends GObject {
        Long uuid();
        record GList (List<GObject> value, Long uuid) implements Heap {}
    }
}
