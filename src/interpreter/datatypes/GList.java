package interpreter.datatypes;

import error.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record GList(List<GObject> value) implements GIndexable {
    public static final String TYPE_NAME = "list";

    @Override
    public String typeName() {
        return TYPE_NAME;
    }

    public GList add(GObject value) {
        value().add(value);
        return this;
    }

    public GList concat(GList other) {
        List<GObject> concatenatedBackingList = new ArrayList<>(value);
        concatenatedBackingList.addAll(other.value());
        return new GList(concatenatedBackingList);
    }

    @Override
    public GBoolean hasIndex(GObject index) {
        Result<GObject, String> validationResult =  validateIndexThenApply(index, ignored -> new GBoolean(true));
        return switch (validationResult) {
            case Result.Success<GObject, String> ignored -> new GBoolean(true);
            case Result.Error<GObject, String> ignored -> new GBoolean(false);
        };
    }

    @Override
    public Result<GObject, String> getAtIndex(GObject index) {
        return validateIndexThenApply(index, i -> value().get(i));
    }

    @Override
    public Result<GObject, String> setAtIndex(GObject index, GObject value) {
        return validateIndexThenApply(index, i -> {
            value().set(i, value);
            return this;
        });
    }

    @Override
    public int getSize() {
        return value().size();
    }

    @Override
    public String stringify() {
        String stringifiedElements = value().stream().map(GObject::stringify).collect(Collectors.joining(","));
        return String.format("[%s]", stringifiedElements);
    }

    private Result<GObject, String> validateIndexThenApply(GObject index, Function<Integer, GObject> function) {
        if (index instanceof GInteger gInt) {
            int i = gInt.value();
            if (i < 0) {
                return Result.error(String.format("List index %d must be non-negative", i));
            } else if (i >= value().size()) {
                return Result.error(String.format("List index '%d' out of bounds for list of length %d", i, value().size()));
            }
            return Result.success(function.apply(i));
        } else {
            return Result.error(String.format("List index '%s' must be an integer", index.stringify()));
        }
    }
}
