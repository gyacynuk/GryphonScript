package parser;

import model.Expression;
import model.Token;

import java.util.List;

public interface Parser {
    public List<Expression> parse(List<Token> tokens);
}
