package org.kie.yard.core;

import org.drools.model.functions.Predicate1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.drools.ruleunits.dsl.patterns.PatternDef;
import org.kie.yard.api.model.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.drools.ruleunits.dsl.Accumulators.*;

public class DoWhen {
    private final YaRDDefinitions definitions;

    public DoWhen(final YaRDDefinitions definitions) {

        this.definitions = definitions;
    }

    public PatternDef doWhen(final YamlRule ruleDefinition,
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

    private Object getGroupingFunction(final Object o,
                                       final GroupBy groupBy,
                                       final YaRDDefinitions definitions) {
        final Grouping grouping = groupBy.getGrouping();
        final Map<String, Object> context = new HashMap<>();
        context.put(groupBy.getGiven().getGiven(), o);
        final MVELLER mveller = new MVELLER(QuotedExprParsed.from(grouping.getBy()));
        return mveller.doTheMVEL(context, definitions);
    }

    private Pattern1Def<Object> getObjectPattern(final RuleFactory rule,
                                                 final Given given) {
        final Pattern1Def<Object> on = rule.on(from(given.getFrom()));
        final String varName = given.getGiven();

        for (String s : given.getHaving()) {
            final String expression = varName + "." + s.trim();
            on.filter((Predicate1<Object>) o -> {
                final Map<String, Object> context = new HashMap<>();
                context.put(varName, o);
                return toBoolean(new MVELLER(QuotedExprParsed.from(expression)).doTheMVEL(context, definitions));
            });
        }
        return on;
    }


    private PatternDef formPattern(final RuleFactory rule,
                                   final Given given) {
        return getObjectPattern(rule, given);
    }

    private DataSource<Object> from(final String from) {
        return definitions.inputs().get(from);
    }

    private boolean toBoolean(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return false;
        }

    }
}
