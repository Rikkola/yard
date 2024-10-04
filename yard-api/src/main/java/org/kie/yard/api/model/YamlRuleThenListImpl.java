package org.kie.yard.api.model;

import java.util.Map;

public class YamlRuleThenListImpl implements YamlRuleThen {
    private Map<String, String> functions;

    public YamlRuleThenListImpl(Map<String, String> functions) {
        this.functions = functions;
    }

    public Map<String, String> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, String> functions) {
        this.functions = functions;
    }
}
