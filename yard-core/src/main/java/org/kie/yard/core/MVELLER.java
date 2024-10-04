package org.kie.yard.core;

import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.Map;

public class MVELLER {

    private final QuotedExprParsed expr;

    public MVELLER(final QuotedExprParsed expr) {
        this.expr = expr;
    }

    public Object doTheMVEL(final Map<String, Object> context,
                            final YaRDDefinitions units) {

        final Map<String, Object> internalContext = new HashMap<>();
        internalContext.putAll(context);

        for (Map.Entry<String, StoreHandle<Object>> outKV : units.outputs().entrySet()) {
            if (!outKV.getValue().isValuePresent()) {
                continue;
            }
            internalContext.put(QuotedExprParsed.escapeIdentifier(outKV.getKey()), outKV.getValue().get());
        }

        try {
            final String rewrittenExpression = expr.getRewrittenExpression();
            return MVEL.eval(rewrittenExpression, internalContext);
        } catch (Exception e) {
            throw new RuntimeException("interpretation failed at runtime", e);
        }
    }
}
