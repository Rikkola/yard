package org.kie.yard.core;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MVELLERTest {

    @Test
    void testSimple() {
        final Map<String, Object> context = new HashMap<>();
        final Map<Object, Object> value = new HashMap<>();
        value.put("name", "Ticket 3");
        value.put("status", "Critical");
        context.put("$ticket", value);
        final String expr = "$ticket.status == \"Blocking\"";
        final Object o = new MVELLER(QuotedExprParsed.from(expr)).doTheMVEL(context, new YaRDDefinitions(new HashMap<>(), null, new HashMap<>()));
        assertEquals(o,false);
    }
    @Test
    void testFirstLetter() {
        final Map<String, Object> context = new HashMap<>();
        final Map<Object, Object> value = new HashMap<>();
        value.put("name", "Lars");
        value.put("age", "24");
        context.put("$p", value);
        final String expr = "$p.age >= 18";
        final Object o = new MVELLER(QuotedExprParsed.from(expr)).doTheMVEL(context, new YaRDDefinitions(new HashMap<>(), null, new HashMap<>()));
        assertEquals(o,true);
    }
}