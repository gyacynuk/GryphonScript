package desugarer;

import model.Expression;

import java.util.List;

public interface Desugarer {
    List<Expression> desugarAll(List<Expression> expressions);
}
