package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import org.drools.util.IoUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

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
        given()
                .header(new Header("Content-Type", "application/json"))
                .body("{ \"yard\":" + yard + ", \"input\":" + input + " }")
                .when().post("/yard")
                .then()
                .statusCode(200)
                .body(is("{\"Bronze Complete\":true,\"Silver Complete\":false,\"Gold Complete\":false,\"Level\":\"Bronze\"}"));
    }

    @Test
    void testHello() {
        HashMap<String, Object> map = new HashMap<>();

        given()
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(is("Hello from Quarkus REST"));
    }

}