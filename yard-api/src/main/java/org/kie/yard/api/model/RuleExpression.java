package org.kie.yard.api.model;

import org.kie.j2cl.tools.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeDeserializer;

import java.util.List;

@YAMLMapper
public class RuleExpression implements DecisionLogic {

//    @YamlTypeDeserializer(YamlRuleDeserializer.class)
    private List<YamlRule> rules;

    public List<YamlRule> getRules() {
        return rules;
    }

    public void setRules(List<YamlRule> rules) {
        this.rules = rules;
    }
}
