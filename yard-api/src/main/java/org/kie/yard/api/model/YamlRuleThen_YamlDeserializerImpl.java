package org.kie.yard.api.model;

import org.kie.j2cl.tools.yaml.mapper.api.YAMLDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.j2cl.tools.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.j2cl.tools.yaml.mapper.api.node.NodeType;
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
        final Map<String, Object> functions = new HashMap<>();
        final YamlSequence result = yamlNode.asMapping().getSequenceNode("result");
        result.iterator().forEachRemaining(x -> {
            for (String key : x.asMapping().keys()) {
                final YamlNode node = x.asMapping().getNode(key);
                if(node.type() == NodeType.SCALAR){
                    functions.put(key, node.asScalar().value());
                } else if( node.type() == NodeType.MAPPING){
                    final YamlMapping mapping = node.asMapping();
                    final Map<String, Object> map = new HashMap<>();
                    map.put("key", mapping.getNode("key").asScalar().value());
                    map.put("value", mapping.getNode("value").asScalar().value());
                    functions.put(key, map);
                }
            }
        });
        return new YamlRuleThenListImpl(functions);
    }
}
