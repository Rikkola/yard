package org.kie.yard.core;

import org.drools.model.Index;
import org.drools.model.functions.Predicate1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.SyntheticRuleUnit;
import org.drools.ruleunits.dsl.SyntheticRuleUnitBuilder;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.kie.yard.api.model.Given;
import org.kie.yard.api.model.RuleExpression;
import org.kie.yard.api.model.YamlRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleExpressionBuilder {
    private final YaRDDefinitions definitions;
    private final List<YamlRule> rules;
    private final String name = "gg";

    public RuleExpressionBuilder(final YaRDDefinitions definitions, final RuleExpression ruleExpression) {
        this.definitions = definitions;
        this.rules = ruleExpression.getRules();
    }

    public SyntheticRuleUnit build() {

        final SyntheticRuleUnitBuilder unit = SyntheticRuleUnitBuilder.build(name);

        for (Map.Entry<String, DataSource<Object>> e : definitions.inputs().entrySet()) {
            unit.registerDataSource(e.getKey(), e.getValue(), Object.class);
        }

        return unit.defineRules(rulesFactory -> {
            for (YamlRule ruleDefinition : rules) {
                final RuleFactory rule = rulesFactory.rule();

                final Map<String, Object> context = getContext();

                for (Given given : ruleDefinition.getWhen()) {
                    Pattern1Def<Object> on = rule.on(from(given.getFrom()));

                    for (String s : given.getHaving()) {
                        final String[] split = split(s);
                        on.filter((Predicate1<Object>) o -> toBoolean(new MVELLER(QuotedExprParsed.from(s)).doTheMVEL(context, definitions)));
                    }
                }

            }

        });
    }

    private Map<String, Object> getContext() {
        final HashMap<String, Object> inputs = new HashMap<>();


        /// TODO inputs will be from inputs of the yard file


        return definitions.evaluate(inputs);
    }

    private boolean toBoolean(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return false;
        }
    }

    private Index.ConstraintType operator(String s) {
        return Index.ConstraintType.EQUAL;
    }

    private String[] split(final String having) {
        return having.split("(?<=(==))|(?=(==))");

    }

    private DataSource<Object> from(final String from) {
        DataSource<Object> ds = definitions.inputs().get(from);
        return ds;
    }
}
