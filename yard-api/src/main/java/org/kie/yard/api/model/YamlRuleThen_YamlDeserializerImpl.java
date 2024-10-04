package org.kie.yard.api.model;

import org.kie.j2cl.tools.yaml.mapper.api.YAMLDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.j2cl.tools.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlMapping;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlNode;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlSequence;

import java.util.HashMap;
import java.util.Map;

public class YamlRuleThen_YamlDeserializerImpl
        implements YAMLDeserializer<YamlRuleThen> {

    @Override
    public YamlRuleThen deserialize(YamlMapping yamlMapping, String s, YAMLDeserializationContext yamlDeserializationContext) throws YAMLDeserializationException {
        return deserialize(yamlMapping.getNode(s), yamlDeserializationContext);
    }

    @Override
    public YamlRuleThen deserialize(YamlNode yamlNode, YAMLDeserializationContext yamlDeserializationContext) {
        final Map<String, String> functions = new HashMap<>();
        final YamlSequence result = yamlNode.asMapping().getSequenceNode("result");
        result.iterator().forEachRemaining(x -> {
            for (String key : x.asMapping().keys()) {
                final YamlNode node = x.asMapping().getNode(key);
                functions.put(key, (String) node.asScalar().value());
            }
        });
        return new YamlRuleThenListImpl(functions);
    }
}
