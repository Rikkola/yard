package org.kie.yard.api.model;


public class GroupBy implements Pattern {
    private Given given;

    private Grouping grouping;

    private Accumulator accumulator;

    public Given getGiven() {
        return given;
    }

    public void setGiven(Given given) {
        this.given = given;
    }

    public Grouping getGrouping() {
        return grouping;
    }

    public void setGrouping(Grouping grouping) {
        this.grouping = grouping;
    }

    public Accumulator getAccumulators() {
        return accumulator;
    }

    public void setAccumulators(Accumulator accumulator) {
        this.accumulator = accumulator;
    }
}
