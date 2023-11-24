package desugarer;

import lombok.RequiredArgsConstructor;
import model.Expression;

import java.util.List;

@RequiredArgsConstructor
public class DesugaringOrchestrator implements Desugarer {
    private final List<Desugarer> desugarers;

    @Override
    public List<Expression> desugarAll(List<Expression> expressions) {
        for (Desugarer desugarer : desugarers) {
            expressions = desugarer.desugarAll(expressions);
        }
        return expressions;
    }
}
