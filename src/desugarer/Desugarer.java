package desugarer;

import model.Expression;

import java.util.List;

// TODO: add an Invocation desugarer so that _(1) us allowed.
// TODO: resolve partial function application as a desugarer
// TODO: make a desugarer orchestrator which runs multiple desugarers
public interface Desugarer {
    List<Expression> desugar(List<Expression> expressions);
}
