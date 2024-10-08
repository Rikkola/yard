package org.kie.yard.core;

import org.drools.model.functions.Predicate1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.SyntheticRuleUnit;
import org.drools.ruleunits.dsl.SyntheticRuleUnitBuilder;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.kie.yard.api.model.Given;
import org.kie.yard.api.model.RuleExpression;
import org.kie.yard.api.model.YamlRule;
import org.kie.yard.api.model.YamlRuleThenListImpl;

import java.util.*;

public class RuleExpressionBuilder {
    private final YaRDDefinitions definitions;
    private final List<YamlRule> rules;
    private final String name;
    private final String resultType;

    public RuleExpressionBuilder(final YaRDDefinitions definitions,
                                 final String name,
                                 final RuleExpression ruleExpression) {
        this.definitions = definitions;
        this.name = name;
        this.resultType = ruleExpression.getResult();
        this.rules = ruleExpression.getRules();
    }

    public SyntheticRuleUnit build() {

        final SyntheticRuleUnitBuilder unit = SyntheticRuleUnitBuilder.build(name);

        for (Map.Entry<String, DataSource<Object>> e : definitions.inputs().entrySet()) {
            unit.registerDataSource(e.getKey(), e.getValue(), Object.class);
        }

        final StoreHandle result = getResult();

        unit.registerGlobal(name, result);
        definitions.outputs().put(name, result);

        return unit.defineRules(rulesFactory -> {
            for (YamlRule ruleDefinition : rules) {
                final RuleFactory rule = rulesFactory.rule();


                final Pattern1Def<Object> on = doWhen(ruleDefinition, rule);
                // TODO take given count, expect that amount of Block
                int givenCount = ruleDefinition.getWhen().size();

                switch (givenCount) {
                    case 1:
                        on.execute(result, (storeHandle, a) -> {
                            final Map<String, Object> context = new HashMap<>();
                            context.put(ruleDefinition.getWhen().get(0).getGiven(), a);
                            doThen(ruleDefinition, storeHandle, context);
                        });

                        // TODO 2 and 3
                }
            }
        });
    }

    private Pattern1Def<Object> doWhen(final YamlRule ruleDefinition,
                                       final RuleFactory rule) {
        final Iterator<Given> iterator = ruleDefinition.getWhen().iterator();
        while (iterator.hasNext()) {
            final Given given = iterator.next();
            final Pattern1Def<Object> on = rule.on(from(given.getFrom()));
            final String varName = given.getGiven();

            for (String s : given.getHaving()) {
                final String expression = varName + "." + s.trim();
                on.filter((Predicate1<Object>) o -> {
                    final Map<String, Object> context = getContext();
                    context.put(varName, o);
                    boolean aBoolean = toBoolean(new MVELLER(QuotedExprParsed.from(expression)).doTheMVEL(context, definitions));
                    return aBoolean;
                });
            }
            if (!iterator.hasNext()) {
                return on;

            }
        }
        return null; // TODO or exception
    }

    private void doThen(final YamlRule ruleDefinition,
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
        }
    }

    private StoreHandle getResult() {
        if (Objects.equals("List", resultType)) {
            final StoreHandle<Object> result = StoreHandle.empty(Object.class);
            result.set(new ArrayList<>());
            return result;
        } else {
            throw new IllegalStateException("Result type is not set correctly");
        }
    }

    private Map<String, Object> getContext() {
        final HashMap<String, Object> inputs = new HashMap<>();
        return inputs;
    }

    private boolean toBoolean(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return false;
        }
    }

    private DataSource<Object> from(final String from) {
        return definitions.inputs().get(from);
    }
}
