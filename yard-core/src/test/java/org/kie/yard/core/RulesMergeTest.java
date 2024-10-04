/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.yard.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RulesMergeTest
        extends TestBase {

    private static final String FILE_NAME = "/merge.yml";

    @Test
    public void testMPackage() throws Exception {
        final String CTX = """
                {
                    "Jira 1":[{"status":"Blocking"},{"status":"Blocking"}],
                    "Jira 2":[{"status":"Blocking"},{"status":"Blocking"}]
                }
                """;
        Map<String, Object> outputJSONasMap = evaluate(CTX, FILE_NAME);
        assertThat(outputJSONasMap).hasFieldOrPropertyWithValue("Merged data from two ticket streams", 40);
    }
}
