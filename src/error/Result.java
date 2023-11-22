package error;

public sealed interface Result<S, E> {
    record Success<S, E>(S value) implements Result<S,E> {}
    record Error<S, E>(E value) implements Result<S, E> {}
    static <S, E> Success<S, E> success(S value) {
        return new Success<S, E>(value);
    }
    static <S, E> Error<S, E> error(E value) {
        return new Error<S, E>(value);
    }
}
