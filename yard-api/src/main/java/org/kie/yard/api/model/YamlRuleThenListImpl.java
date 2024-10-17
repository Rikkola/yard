package org.kie.yard.api.model;

import java.util.Map;

public class YamlRuleThenListImpl implements YamlRuleThen {
    private Map<String, Object> functions;

    public YamlRuleThenListImpl(Map<String, Object> functions) {
        this.functions = functions;
    }

    public Map<String, Object> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, Object> functions) {
        this.functions = functions;
    }
}
