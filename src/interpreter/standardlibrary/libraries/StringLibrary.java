package interpreter.standardlibrary.libraries;

import com.google.inject.Singleton;
import interpreter.datatypes.*;
import interpreter.standardlibrary.Library;
import interpreter.standardlibrary.LibraryFunction;
import interpreter.standardlibrary.TypeCastUtils;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Singleton
@NoArgsConstructor
public class StringLibrary implements Library {
    @Override
    public List<String> getLibraryPath() {
        return Collections.singletonList("String");
    }

    @Override
    public List<LibraryFunction> getFunctions() {
        return Arrays.asList(
                length(), stringify(), parseInteger(), parseDouble(), parseBoolean(),
                substring(), stripWhitespace(), split(), replaceFirst(), replaceAll());
    }

    private LibraryFunction length() {
        final String lambdaName = "length";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            return new GInteger(gString.getSize());
        });
    }

    private LibraryFunction stringify() {
        final String lambdaName = "stringify";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> new GString(args.get(0).stringify()));
    }

    private LibraryFunction parseInteger() {
        final String lambdaName = "parseInteger";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            try {
                return new GInteger(Integer.parseInt(gString.value()));
            } catch (Exception e) {
                return GNil.INSTANCE;
            }
        });
    }

    private LibraryFunction parseDouble() {
        final String lambdaName = "parseDouble";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            try {
                return new GDouble(Double.parseDouble(gString.value()));
            } catch (Exception e) {
                return GNil.INSTANCE;
            }
        });
    }

    private LibraryFunction parseBoolean() {
        final String lambdaName = "parseBoolean";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            try {
                return new GBoolean(Boolean.parseBoolean(gString.value()));
            } catch (Exception e) {
                return GNil.INSTANCE;
            }
        });
    }

    private LibraryFunction substring() {
        final String lambdaName = "substring";
        final int arity = 3;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            GInteger start = TypeCastUtils.toGInteger(args, 1, lambdaName);
            GInteger end = TypeCastUtils.toGInteger(args, 2, lambdaName);

            String substring = gString.value().substring(start.value(), end.value());
            return new GString(substring);
        });
    }

    private LibraryFunction stripWhitespace() {
        final String lambdaName = "stripWhitespace";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            String strippedString = gString.value().strip();
            return new GString(strippedString);
        });
    }

    private LibraryFunction split() {
        final String lambdaName = "split";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            GString pattern = TypeCastUtils.toGString(args, 1, lambdaName);
            List<GObject> splitStrings = Arrays.stream(gString.value().split(pattern.value()))
                    .map(str -> (GObject) new GString(str))
                    .toList();
            return new GList(splitStrings);
        });
    }

    private LibraryFunction replaceFirst() {
        final String lambdaName = "replaceFirst";
        final int arity = 3;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            GString pattern = TypeCastUtils.toGString(args, 1, lambdaName);
            GString replacement = TypeCastUtils.toGString(args, 2, lambdaName);
            String replacedString = gString.value().replaceFirst(pattern.value(), replacement.value());
            return new GString(replacedString);
        });
    }

    private LibraryFunction replaceAll() {
        final String lambdaName = "replaceAll";
        final int arity = 3;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            GString pattern = TypeCastUtils.toGString(args, 1, lambdaName);
            GString replacement = TypeCastUtils.toGString(args, 2, lambdaName);
            String replacedString = gString.value().replaceAll(pattern.value(), replacement.value());
            return new GString(replacedString);
        });
    }
}
