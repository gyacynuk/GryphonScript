package interpreter.standardlibrary.libraries;

import com.google.inject.Singleton;
import interpreter.InterpreterUtils;
import interpreter.datatypes.*;
import interpreter.standardlibrary.Library;
import interpreter.standardlibrary.LibraryFunction;
import interpreter.standardlibrary.TypeCastUtils;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Singleton
@NoArgsConstructor
public class ListLibrary implements Library {
    @Override
    public List<String> getLibraryPath() {
        return Collections.singletonList("List");
    }

    @Override
    public List<LibraryFunction> getFunctions() {
        return Arrays.asList(
                size(), append(), sublist(), reversed(), zip(),
                take(), filter(), map(), fold(), accumulate());
    }

    private LibraryFunction size() {
        final String lambdaName = "size";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            return new GInteger(list.getSize());
        });
    }

    private LibraryFunction append() {
        final String lambdaName = "append";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            return list.add(args.get(1));
        });
    }

    private LibraryFunction sublist() {
        final String lambdaName = "sublist";
        final int arity = 3;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            GInteger start = TypeCastUtils.toGInteger(args, 1, lambdaName);
            GInteger end = TypeCastUtils.toGInteger(args, 2, lambdaName);

            List<GObject> sublist = new ArrayList<>(list.value().subList(start.value(), end.value()));
            return new GList(sublist);
        });
    }

    private LibraryFunction reversed() {
        final String lambdaName = "reversed";
        final int arity = 1;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            List<GObject> reversed = new ArrayList<>(list.value().reversed());
            return new GList(reversed);
        });
    }

    private LibraryFunction zip() {
        final String lambdaName = "zip";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList listA = TypeCastUtils.toGList(args, 0, lambdaName);
            GList listB = TypeCastUtils.toGList(args, 1, lambdaName);

            int maxLength = Math.max(listA.getSize(), listB.getSize());
            List<GObject> zippedList = new ArrayList<>();
            for (int i = 0; i < maxLength; i ++) {
                List<GObject> zippedPair = new ArrayList<>();
                zippedPair.add(i < listA.getSize() ? listA.value().get(i) : GNil.INSTANCE);
                zippedPair.add(i < listB.getSize() ? listB.value().get(i) : GNil.INSTANCE);
                zippedList.add(new GList(zippedPair));
            }

            return new GList(zippedList);
        });
    }

    private LibraryFunction take() {
        final String lambdaName = "take";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            GInteger numElements = TypeCastUtils.toGInteger(args, 1, lambdaName);

            List<GObject> sublist = new ArrayList<>(list.value().subList(0, numElements.value()));
            return new GList(sublist);
        });
    }

    private LibraryFunction filter() {
        final String lambdaName = "filter";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            GLambda predicate = TypeCastUtils.toGLambda(args, 1, lambdaName);

            // Implement using a for-loop instead of using Stream APIs for performance improvement
            List<GObject> filteredList = new ArrayList<>();
            for (GObject element : list.value()) {
                if (InterpreterUtils.isTruthy(predicate.value().call(interpreter, Collections.singletonList(element)))) {
                    filteredList.add(element);
                }
            }

            return new GList(filteredList);
        });
    }

    private LibraryFunction map() {
        final String lambdaName = "map";
        final int arity = 2;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            GLambda function = TypeCastUtils.toGLambda(args, 1, lambdaName);

            // Implement using a for-loop instead of using Stream APIs for performance improvement
            List<GObject> mappedList = new ArrayList<>();
            for (GObject element : list.value()) {
                mappedList.add(function.value().call(interpreter, Collections.singletonList(element)));
            }

            return new GList(mappedList);
        });
    }

    private LibraryFunction fold() {
        final String lambdaName = "fold";
        final int arity = 3;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            GObject accumulator = args.get(1);
            GLambda function = TypeCastUtils.toGLambda(args, 2, lambdaName);

            if (!list.value().isEmpty()) {
                for (GObject element : list.value()) {
                    List<GObject> accumulatorFunctionArguments = Arrays.asList(accumulator, element);
                    accumulator = function.value().call(interpreter, accumulatorFunctionArguments);
                }
            }
            return accumulator;
        });
    }

    private LibraryFunction accumulate() {
        final String lambdaName = "accumulate";
        final int arity = 3;
        return new LibraryFunction(lambdaName, arity, (interpreter, args) -> {
            GList list = TypeCastUtils.toGList(args, 0, lambdaName);
            GObject accumulator = args.get(1);
            GLambda function = TypeCastUtils.toGLambda(args, 2, lambdaName);

            // Implement using a for-loop instead of using Stream APIs for performance improvement
            List<GObject> mappedList = new ArrayList<>();
            for (GObject element : list.value()) {
                List<GObject> accumulatorFunctionArguments = Arrays.asList(accumulator, element);
                accumulator = function.value().call(interpreter, accumulatorFunctionArguments);
                mappedList.add(accumulator);
            }

            return new GList(mappedList);
        });
    }
}
