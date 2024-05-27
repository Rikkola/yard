package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import org.drools.util.IoUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class YaRDResourceTest {

    @Test
    void testHelloEndpoint() throws IOException {
        final String yard = new String(IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/simplified-ticket-score.json"), true));

        final String input = """
                {
                     "Bronze Complete" : true,
                     "Silver Complete" : false,
                     "Gold Complete" : false
                 }
                 """;
        String builderString = new StringBuilder().append("{ \"yard\":").append(yard).append(", \"input\":").append(input).append(" }").toString();
        System.out.println(builderString);
        String result = "{\"Bronze Complete\":true,\"Silver Complete\":false,\"Gold Complete\":false,\"Level\":\"Bronze\"}";

        given()
                .header(new Header("Content-Type", "application/json"))
                .body(builderString)
                .when().post("/yard")
                .then()
                .statusCode(200)
                .body(is(result));
    }
}