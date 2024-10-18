package org.kie.yard.core;

import org.kie.yard.api.model.YamlRule;
import org.kie.yard.api.model.YamlRuleThenListImpl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DoThen {
    private final String resultType;
    private final YaRDDefinitions definitions;

    public DoThen(final String resultType,
                  final YaRDDefinitions definitions) {
        this.resultType = resultType;
        this.definitions = definitions;
    }

    public void doThen(final YamlRule ruleDefinition,
                       final StoreHandle storeHandle,
                       final Map<String, Object> context) {

        if (Objects.equals("List", resultType)) {
            if (storeHandle.get() instanceof List list) {
                if (ruleDefinition.getThen() instanceof YamlRuleThenListImpl thenList) {
                    if (thenList.getFunctions().containsKey("add")) {
                        list.add(context.get(thenList.getFunctions().get("add")));
                    }
                }
            }
        } else if (Objects.equals("Map", resultType)) {
            if (storeHandle.get() instanceof Map map) {
                if (ruleDefinition.getThen() instanceof YamlRuleThenListImpl thenList) {
                    if (thenList.getFunctions().containsKey("put")) {
                        final Map<String, String> o = (Map<String, String>) thenList.getFunctions().get("put");
                        final Object key = new MVELLER(QuotedExprParsed.from(o.get("key"))).doTheMVEL(context, definitions);
                        final Object value = new MVELLER(QuotedExprParsed.from(o.get("value"))).doTheMVEL(context, definitions);
                        map.put(key, value);
                    }
                }
            }
        }    }
}
