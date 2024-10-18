package org.kie.yard.api.model;

import org.kie.j2cl.tools.yaml.mapper.api.annotation.YAMLMapper;

import java.util.List;

@YAMLMapper
public class RuleExpression implements DecisionLogic {

    private String result;

    private List<YamlRule> rules;

    public List<YamlRule> getRules() {
        return rules;
    }

    public void setRules(List<YamlRule> rules) {
        this.rules = rules;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
