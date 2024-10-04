package org.kie.yard.api.model;

import org.kie.j2cl.tools.yaml.mapper.api.YAMLDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.j2cl.tools.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlMapping;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlNode;

public class YamlRuleDeserializer implements YAMLDeserializer<YamlRule> {
    @Override
    public YamlRule deserialize(YamlMapping yamlMapping, String s, YAMLDeserializationContext yamlDeserializationContext) throws YAMLDeserializationException {
        return new YamlRule();
    }

    @Override
    public YamlRule deserialize(YamlNode yamlNode, YAMLDeserializationContext yamlDeserializationContext) {
        return new YamlRule();
    }
}
