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
public class MathLibrary implements Library {
    @Override
    public List<String> getLibraryPath() {
        return Collections.singletonList("Math");
    }

    @Override
    public List<LibraryFunction> getFunctions() {
        return Arrays.asList(
                floor(), ceiling(),
                max(), min());
    }

    private LibraryFunction floor() {
        final String lambdaName = "floor";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GNumeric numeric = TypeCastUtils.toGNumeric(args, 0, lambdaName);
            return switch (numeric) {
                case GDouble gDouble -> new GInteger((int) Math.floor(gDouble.value()));
                case GInteger gInteger -> gInteger;
            };
        });
    }

    private LibraryFunction ceiling() {
        final String lambdaName = "ceiling";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GNumeric numeric = TypeCastUtils.toGNumeric(args, 0, lambdaName);
            return switch (numeric) {
                case GDouble gDouble -> new GInteger((int) Math.ceil(gDouble.value()));
                case GInteger gInteger -> gInteger;
            };
        });
    }

    private LibraryFunction max() {
        final String lambdaName = "max";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GNumeric numericA = TypeCastUtils.toGNumeric(args, 0, lambdaName);
            GNumeric numericB = TypeCastUtils.toGNumeric(args, 1, lambdaName);
            return numericA.greaterThanOrEqualTo(numericB).value()
                    ? numericA
                    : numericB;
        });
    }

    private LibraryFunction min() {
        final String lambdaName = "min";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GNumeric numericA = TypeCastUtils.toGNumeric(args, 0, lambdaName);
            GNumeric numericB = TypeCastUtils.toGNumeric(args, 1, lambdaName);
            return numericA.lessThanOrEqualTo(numericB).value()
                    ? numericA
                    : numericB;
        });
    }
}
