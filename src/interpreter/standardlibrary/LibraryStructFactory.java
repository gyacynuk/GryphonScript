package interpreter.standardlibrary;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import error.Result;
import interpreter.Interpreter;
import interpreter.datatypes.*;
import interpreter.lambda.Invokable;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class LibraryStructFactory {
    private final List<Library> libraries;

    public String getStandardLibraryName() {
        return "StdLib";
    }

    public GStruct buildStandardLibraryStruct() {
        GStruct standardLibrary = GStruct.initEmptyStruct();
        libraries.forEach(library ->
                library.getFunctions().forEach(libraryFunction ->
                        addLibraryLambda(standardLibrary, libraryFunction, library.getLibraryPath())));
        return standardLibrary;
    }

    private void addLibraryLambda(GStruct library, LibraryFunction libraryFunction, List<String> path) {
        if (path.isEmpty()) {
            GObject lambdaName = new GString(libraryFunction.name());
            GObject lambdaFunction = libraryFunctionToGLambda(libraryFunction);
            library.setAtIndex(lambdaName, lambdaFunction);
        } else {
            GObject subLibraryName = new GString(path.get(0));
            Result<GObject, String> subLibraryLookupResult = library.getAtIndex(subLibraryName);

            GStruct subLibrary;
            if (subLibraryLookupResult instanceof Result.Success<GObject, String> successfulLookup
                    && !Objects.equals(successfulLookup.value(), GNil.INSTANCE)) {
                subLibrary = (GStruct) successfulLookup.value();
            } else {
                subLibrary = GStruct.initEmptyStruct();
                library.setAtIndex(subLibraryName, subLibrary);
            }

            addLibraryLambda(subLibrary, libraryFunction, path.subList(1, path.size()));
        }
    }

    private GLambda libraryFunctionToGLambda(LibraryFunction libraryFunction) {
        return new GLambda(new Invokable() {
            @Override
            public int arity() {
                return libraryFunction.arity();
            }

            @Override
            public GObject call(Interpreter interpreter, List<GObject> arguments) {
                return libraryFunction.function().apply(interpreter, arguments);
            }
        });
    }
}
