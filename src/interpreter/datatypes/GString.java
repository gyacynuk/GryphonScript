package interpreter.datatypes;

import error.Result;

import java.util.function.Function;

public record GString(String value) implements GObject, GIndexable {
    @Override
    public Result<GObject, String> getAtIndex(GObject index) {
        return validateIndexThenApply(index, i -> new GString(String.valueOf(value.charAt(i))));
    }

    @Override
    public Result<GObject, String> setAtIndex(GObject index, GObject value1) {
        return Result.error("Cannot set a string index to a value");
    }

    @Override
    public int getSize() {
        return value.length();
    }

    private Result<GObject, String> validateIndexThenApply(GObject index, Function<Integer, GObject> function) {
        if (index instanceof GInteger gInt) {
            int i = gInt.value();
            if (i < 0) {
                return Result.error(String.format("String index %d must be non-negative", i));
            } else if (i >= getSize()) {
                return Result.error(String.format("String index '%d' out of bounds for String of length %d", i, getSize()));
            }
            return Result.success(function.apply(i));
        } else {
            return Result.error(String.format("String index '%s' must be an integer", index.stringify()));
        }
    }
}
