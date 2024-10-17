package org.kie.yard.api.model;

import org.kie.j2cl.tools.yaml.mapper.api.YAMLSerializer;
import org.kie.j2cl.tools.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlMapping;
import org.kie.j2cl.tools.yaml.mapper.api.node.YamlSequence;

public class When_YamlSerializerImpl
        implements YAMLSerializer<When> {
    @Override
    public void serialize(YamlMapping yamlMapping, String s, When when, YAMLSerializationContext yamlSerializationContext) {

    }

    @Override
    public void serialize(YamlSequence yamlSequence, When when, YAMLSerializationContext yamlSerializationContext) {

    }
}
