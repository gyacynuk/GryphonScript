package interpreter.standardlibrary.libraries;

import com.google.inject.Singleton;
import interpreter.datatypes.GList;
import interpreter.datatypes.GObject;
import interpreter.datatypes.GString;
import interpreter.lambda.InvocationExecutionError;
import interpreter.standardlibrary.Library;
import interpreter.standardlibrary.LibraryFunction;
import interpreter.standardlibrary.TypeCastUtils;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Singleton
@NoArgsConstructor
public class FileLibrary implements Library {
    @Override
    public List<String> getLibraryPath() {
        return Collections.singletonList("File");
    }

    @Override
    public List<LibraryFunction> getFunctions() {
        return Arrays.asList(readLines());
    }

    private LibraryFunction readLines() {
        final String lambdaName = "readLines";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GString fileName = TypeCastUtils.toGString(args, 0, lambdaName);

            try (Stream<String> stream = Files.lines(Paths.get(fileName.value()))) {
                return new GList(stream
                        .map(line -> (GObject) new GString(line))
                        .toList());
            } catch (IOException e) {
                throw new InvocationExecutionError("Lambda '%s' encountered an IO error while reading file: %s"
                        .formatted(lambdaName, e.getMessage()));
            }
        });
    }
}
