package interpreter.standardlibrary.libraries;

import com.google.inject.Singleton;
import interpreter.datatypes.GDouble;
import interpreter.datatypes.GObject;
import interpreter.standardlibrary.Library;
import interpreter.standardlibrary.LibraryFunction;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Singleton
@NoArgsConstructor
public class RootLibrary implements Library {
    @Override
    public List<String> getLibraryPath() {
        return Collections.emptyList();
    }

    @Override
    public List<LibraryFunction> getFunctions() {
        return Arrays.asList(
                print(),
                milliTime());
    }

    private LibraryFunction print() {
        final String lambdaName = "print";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GObject arg = args.get(0);
            System.out.println(arg.stringify());
            return arg;
        });
    }

    private LibraryFunction milliTime() {
        final String lambdaName = "milliTime";
        final int arity = 0;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) ->
                new GDouble((double) System.currentTimeMillis()));
    }
}
