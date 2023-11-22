package tokenizer;

import model.Token;

import java.util.List;

public interface Tokenizer {
    List<Token> tokenize(String source);
}
