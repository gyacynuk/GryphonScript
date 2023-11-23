package desugarer;

import lombok.RequiredArgsConstructor;
import model.Expression;

import java.util.List;

@RequiredArgsConstructor
public class DesugaringOrchestrator implements Desugarer {
    private final List<Desugarer> desugarers;

    @Override
    public List<Expression> desugar(List<Expression> expressions) {
        for (Desugarer desugarer : desugarers) {
            expressions = desugarer.desugar(expressions);
        }
        return expressions;
    }
}
