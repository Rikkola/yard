package org.kie.yard.api.model;

import java.util.List;

public class Given {
    private String given;
    private String from;
    private List<String> having;

    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getHaving() {
        return having;
    }

    public void setHaving(List<String> having) {
        this.having = having;
    }
}
