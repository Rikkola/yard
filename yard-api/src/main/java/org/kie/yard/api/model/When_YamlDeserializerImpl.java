package org.kie.yard.api.model;

import org.kie.j2cl.tools.yaml.mapper.api.YAMLDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.j2cl.tools.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.j2cl.tools.yaml.mapper.api.node.NodeType;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlMapping;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlNode;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlSequence;

import java.util.ArrayList;
import java.util.List;

public class When_YamlDeserializerImpl
        implements YAMLDeserializer<When> {

    @Override
    public When deserialize(YamlMapping yamlMapping, String s, YAMLDeserializationContext yamlDeserializationContext) throws YAMLDeserializationException {
        return deserialize(yamlMapping.getNode(s), yamlDeserializationContext);
    }

    @Override
    public When deserialize(YamlNode yamlNode, YAMLDeserializationContext yamlDeserializationContext) {
        final NodeType type = yamlNode.type();
        final YamlMapping mapping = yamlNode.asMapping();
        if (mapping.keys().contains("given")) {
            return createGiven(mapping);
        } else if (mapping.keys().contains("groupBy")) {
            final GroupBy groupBy = new GroupBy();
            groupBy.setGiven(createGiven(mapping.getNode("groupBy").asSequence()));
            groupBy.setGrouping(createGrouping(mapping.getNode("grouping").asMapping()));
            groupBy.setAccumulators(createAccumulators(mapping.getNode("accumulators").asSequence()));
            return groupBy;
        }
        throw new IllegalStateException("Unknown element, should be given or groupBy");
    }

    private List<Accumulator> createAccumulators(YamlSequence groupBy) {
        final ArrayList<Accumulator> accumulators = new ArrayList<>();
        for (YamlNode yamlNode : groupBy) {
            final Accumulator accumulator = new Accumulator();
            accumulator.setFunction((String)yamlNode.asMapping().getNode("function").asScalar().value());
            accumulator.setAs((String)yamlNode.asMapping().getNode("as").asScalar().value());
            accumulators.add(accumulator);
        }

        return accumulators;
    }

    private List<Given> createGiven(YamlSequence groupBy) {
        final ArrayList<Given> result = new ArrayList<>();
        for (YamlNode yamlNode : groupBy) {
            result.add(createGiven(yamlNode.asMapping()));
        }

        return result;
    }

    private static Given createGiven(YamlMapping mapping) {
        final Given given = new Given();
        given.setGiven((String) mapping.getNode("given").asScalar().value());
        given.setFrom((String) mapping.getNode("from").asScalar().value());
        given.setHaving(new ArrayList<>());
        for (YamlNode having : mapping.getNode("having").asSequence()) {
            given.getHaving().add((String) having.asScalar().value());
        }
        return given;
    }

    private Grouping createGrouping(YamlMapping groupingNode) {
        final Grouping grouping = new Grouping();
        grouping.setFunction((String)groupingNode.getNode("function").asScalar().value());
        grouping.setAs((String)groupingNode.getNode("as").asScalar().value());
        return grouping;
    }
}
