package org.kie.yard.api.model;

import org.kie.j2cl.tools.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeSerializer;

import java.util.List;

@YAMLMapper
public class YamlRule {

    @YamlTypeDeserializer(Pattern_YamlDeserializerImpl.class)
    @YamlTypeSerializer(Pattern_YamlSerializerImpl.class)
    private List<Pattern> when;

    @YamlTypeDeserializer(YamlRuleThen_YamlDeserializerImpl.class)
    @YamlTypeSerializer(YamlRuleThen_YamlSerializerImpl.class)
    private YamlRuleThen then;

    public List<Pattern> getWhen() {
        return when;
    }

    public void setWhen(List<Pattern> when) {
        this.when = when;
    }

    public YamlRuleThen getThen() {
        return then;
    }

    public void setThen(YamlRuleThen then) {
        this.then = then;
    }
}
