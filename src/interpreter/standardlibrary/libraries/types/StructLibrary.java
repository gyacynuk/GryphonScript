package interpreter.standardlibrary.libraries.types;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import interpreter.datatypes.GList;
import interpreter.datatypes.GObject;
import interpreter.datatypes.GStruct;
import interpreter.standardlibrary.Library;
import interpreter.standardlibrary.LibraryFunction;
import interpreter.standardlibrary.TypeCastUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class StructLibrary implements Library {
    private final TypeLibrary typeLibrary;

    @Override
    public List<String> getLibraryPath() {
        List<String> path = new ArrayList<>(typeLibrary.getLibraryPath());
        path.add("Struct");
        return path;
    }

    @Override
    public List<LibraryFunction> getFunctions() {
        return Arrays.asList(
                keys(), values(), entries(),
                hasKey());
    }

    private LibraryFunction keys() {
        final String lambdaName = "keys";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GStruct struct = TypeCastUtils.toGStruct(args, 0, lambdaName);
            return new GList(new ArrayList<>(struct.value().keySet()));
        });
    }

    private LibraryFunction values() {
        final String lambdaName = "values";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GStruct struct = TypeCastUtils.toGStruct(args, 0, lambdaName);
            return new GList(new ArrayList<>(struct.value().values()));
        });
    }

    private LibraryFunction entries() {
        final String lambdaName = "entries";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GStruct struct = TypeCastUtils.toGStruct(args, 0, lambdaName);
            return new GList(
                    struct.value().entrySet()
                            .stream()
                            .map(e -> (GObject) new GList(new ArrayList<>(Arrays.asList(e.getKey(), e.getValue()))))
                            .toList());
        });
    }

    private LibraryFunction hasKey() {
        final String lambdaName = "hasKey";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GStruct struct = TypeCastUtils.toGStruct(args, 0, lambdaName);
            GObject index = args.get(1);
            return struct.hasIndex(index);
        });
    }
}
