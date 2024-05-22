package org.acme;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.kie.yard.core.YaRDRunner;

import java.io.IOException;
import java.util.Map;

@Path("/yard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class YaRDResource {

    @POST
    public Map<String, Object> yard(Map<String, Object> payload) throws IOException {

        if (!payload.containsKey("yard") || !payload.containsKey("input")) {
            throw new IllegalArgumentException("Input has to have 'yard' and input 'input'.");
        }

        final String yaml = (String) payload.get("yard");
        final Map<String, Object> input = getInput(payload.get("input"));

        return new YaRDRunner(yaml).evaluate(input);
    }

    private Map<String, Object> getInput(Object input) {
        return (Map<String,Object>) input;
    }
}
