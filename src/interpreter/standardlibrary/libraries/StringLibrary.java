package interpreter.standardlibrary.libraries;

import com.google.inject.Singleton;
import interpreter.datatypes.GInteger;
import interpreter.datatypes.GList;
import interpreter.datatypes.GObject;
import interpreter.datatypes.GString;
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
                substring(), trim(), split(), replaceFirst(), replaceAll());
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

    private LibraryFunction trim() {
        final String lambdaName = "trim";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString gString = TypeCastUtils.toGString(args, 0, lambdaName);
            String trimmedString = gString.value().trim();
            return new GString(trimmedString);
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
