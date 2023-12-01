package interpreter.standardlibrary.libraries.types;

import com.google.inject.Singleton;
import interpreter.datatypes.GString;
import interpreter.standardlibrary.Library;
import interpreter.standardlibrary.LibraryFunction;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Singleton
@NoArgsConstructor
public class TypeLibrary implements Library {
    @Override
    public List<String> getLibraryPath() {
        return Collections.singletonList("Type");
    }

    @Override
    public List<LibraryFunction> getFunctions() {
        return Arrays.asList(typeOf());
    }

    private LibraryFunction typeOf() {
        final String lambdaName = "typeOf";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) ->
                new GString(args.get(0).typeName()));
    }
}
