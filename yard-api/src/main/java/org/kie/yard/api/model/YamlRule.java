package org.kie.yard.api.model;

import java.util.List;

public class YamlRule {

    private List<Given> when;
    private String then;

    public List<Given> getWhen() {
        return when;
    }

    public void setWhen(List<Given> when) {
        this.when = when;
    }

    public String getThen() {
        return then;
    }

    public void setThen(String then) {
        this.then = then;
    }
}
