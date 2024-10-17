package org.kie.yard.core;

import org.drools.model.functions.Predicate1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.SyntheticRuleUnit;
import org.drools.ruleunits.dsl.SyntheticRuleUnitBuilder;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.accumulate.GroupByPattern1;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.drools.ruleunits.dsl.patterns.PatternDef;
import org.kie.yard.api.model.*;

import java.util.*;

import static org.drools.ruleunits.dsl.Accumulators.*;

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
            for (final YamlRule ruleDefinition : rules) {
                final RuleFactory rule = rulesFactory.rule();


                final PatternDef on = doWhen(ruleDefinition, rule);
                // TODO take given count, expect that amount of Block
                final int givenCount = ruleDefinition.getWhen().size();

                switch (givenCount) {
                    case 1:
                        if (on instanceof Pattern1Def on1) {
                            ((Pattern1Def<Object>) on1).execute(result, (storeHandle, a) -> {
                                final Map<String, Object> context = new HashMap<>();
                                if (ruleDefinition.getWhen().get(0) instanceof Given given) {
                                    context.put(given.getGiven(), a);
                                }
                                doThen(ruleDefinition, storeHandle, context);
                            });
                        } else if (on instanceof GroupByPattern1 on1) {
                            final GroupBy groupBy = (GroupBy) ruleDefinition.getWhen().get(0); // TODO 0?
                            ((GroupByPattern1<Object, Object, Object>) on1)
                                    .execute(result, (storeHandle, grouper, group) -> {
                                        final Map<String, Object> context = new HashMap<>();
                                        context.put(groupBy.getGrouping().getAs(), grouper);
                                        context.put(groupBy.getAccumulators().getAs(), group);
                                        doThen(ruleDefinition, storeHandle, context);
                                    });
                        }
                        // TODO 2 and 3
                }
            }
        });
    }

    private PatternDef doWhen(final YamlRule ruleDefinition,
                              final RuleFactory rule) {
        final Iterator<When> iterator = ruleDefinition.getWhen().iterator();
        while (iterator.hasNext()) {
            final When when = iterator.next();
            if (when instanceof Given given) {
                final Pattern1Def<Object> on = getObjectPattern(rule, given);
                if (!iterator.hasNext()) {
                    return on;
                }
            }
            if (when instanceof GroupBy groupBy) {
                final Given given = groupBy.getGiven();
                return rule.groupBy(
                        r -> formPattern(r, given),
                        o -> getGroupingFunction(o, groupBy, definitions),
                        getAccumulator(groupBy, definitions)

                );
            }
            throw new IllegalStateException("Unknown when section.");
        }
        // TODO is this the best approach?
        throw new IllegalStateException("What happened?");
    }

    private static Accumulator1 getAccumulator(final GroupBy groupBy, YaRDDefinitions definitions) {
        final Accumulator accumulator = groupBy.getAccumulators();
        final String function = accumulator.getFunction();
        final String functionParameter = accumulator.getParameter();
        if (functionParameter == null || functionParameter.trim().isEmpty()) {
            return switch (function) {
                case "count" -> count();
                case "collect" -> collect();
                default -> throw new IllegalStateException("Could not find function " + function);
            };
        } else {
            return switch (function) {
                case "sum" -> sum((a) -> {
                    final Map<String, Object> context = new HashMap<>();
                    context.put(groupBy.getGiven().getGiven(), a);
                    return Integer.parseInt((String) new MVELLER(QuotedExprParsed.from(functionParameter)).doTheMVEL(context, definitions));
                });
                default ->
                        throw new IllegalStateException("Could not find function " + function + " with a parameter.");
            };
        }
    }

    private PatternDef formPattern(final RuleFactory rule,
                                   final Given given) {
        return getObjectPattern(rule, given);
    }

    private Object getGroupingFunction(final Object o,
                                       final GroupBy groupBy,
                                       final YaRDDefinitions definitions) {
        final Grouping grouping = groupBy.getGrouping();
        final Map<String, Object> context = getContext();
        context.put(groupBy.getGiven().getGiven(), o);
        final MVELLER mveller = new MVELLER(QuotedExprParsed.from(grouping.getFunction()));
        return mveller.doTheMVEL(context, definitions);
    }

    private Pattern1Def<Object> getObjectPattern(final RuleFactory rule,
                                                 final Given given) {
        final Pattern1Def<Object> on = rule.on(from(given.getFrom()));
        final String varName = given.getGiven();

        for (String s : given.getHaving()) {
            final String expression = varName + "." + s.trim();
            on.filter((Predicate1<Object>) o -> {
                final Map<String, Object> context = getContext();
                context.put(varName, o);
                return toBoolean(new MVELLER(QuotedExprParsed.from(expression)).doTheMVEL(context, definitions));
            });
        }
        return on;
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
        }
    }

    private StoreHandle getResult() {
        if (Objects.equals("List", resultType)) {
            final StoreHandle<Object> result = StoreHandle.empty(Object.class);
            result.set(new ArrayList<>());
            return result;
        } else if (Objects.equals("Map", resultType)) {
            final StoreHandle<Object> result = StoreHandle.empty(Object.class);
            result.set(new HashMap<>());
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
