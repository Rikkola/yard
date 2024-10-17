package org.kie.yard.api.model;

import java.util.List;

public class GroupBy implements When {
    private List<Given> given;

    private Grouping grouping;

    private List<Accumulator> accumulators;

    public List<Given> getGiven() {
        return given;
    }

    public void setGiven(List<Given> given) {
        this.given = given;
    }

    public Grouping getGrouping() {
        return grouping;
    }

    public void setGrouping(Grouping grouping) {
        this.grouping = grouping;
    }

    public List<Accumulator> getAccumulators() {
        return accumulators;
    }

    public void setAccumulators(List<Accumulator> accumulators) {
        this.accumulators = accumulators;
    }
}
