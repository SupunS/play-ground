/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package json.tests;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class JacksonPerfTest {

    public static void main(String[] args) throws Exception {

        JacksonPerfTest test = new JacksonPerfTest();
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/json-samples/10K-sample.json"));
        String json = new String(encoded, "UTF-8");

        JsonNode nthElement = null;
        long startTime = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; i < 10000000; i++) {

            // Change this accordingly
            nthElement = test.printNthElement(json);
            count++;
            if ((i % 500000) == 0) {
                long endTime = System.currentTimeMillis();
                double duration = (endTime - startTime);
                System.out.println(count + "/" + duration);
                System.out.println("Single Exection Time: " + duration / count);
                System.out.println("Traversal Rate: " + count / duration * 1000);
                startTime = System.currentTimeMillis();
                count = 0;
            }
        }
        System.out.println(nthElement);
    }

    /**
     * print some random element of a json array, with streaming+tree model
     */
    private JsonNode printNthElement(String json) throws IOException {
        JsonFactory jfactory = new JsonFactory();
        jfactory.setCodec(new ObjectMapper());
        JsonParser jParser = jfactory.createParser(json);
        JsonNode element = null;
        while (true) {
            JsonToken token = jParser.nextToken();
            if (token == null) {
                break;
            }
            if (token.equals(JsonToken.START_ARRAY)) {

                // TODO Change this accordingly
                element = printLastElement(jParser);
                break;
            }
        }
        // System.out.println("Done!");
        return element;
    }

    /**
     * Read the first element of a array
     */
    private JsonNode printFirstElement(JsonParser jParser) throws IOException {
        JsonNode element = null;
        while (true) {
            JsonToken token = jParser.nextToken();
            if (token.equals(JsonToken.END_ARRAY)) {
                break;
            } else if (token.equals(JsonToken.START_OBJECT)) {
                element = jParser.readValueAsTree();
                break;
            } else {
                continue;
            }
        }
        // System.out.println(arrayIndex + " th Element: \n" + element);
        return element;
    }

    /**
     * Read last element of a array
     * 
     * @return
     */
    private JsonNode printLastElement(JsonParser jParser) throws IOException {
        JsonNode element = null;
        while (true) {
            JsonToken token = jParser.nextToken();
            if (token.equals(JsonToken.END_ARRAY)) {
                break;
            } else if (token.equals(JsonToken.START_OBJECT)) {
                element = jParser.readValueAsTree();
            } else {
                continue;
            }
        }
        // System.out.println(arrayIndex + " th Element: \n" + element);
        return element;
    }

    /**
     * Read last element of a array
     * 
     * @return
     */
    private JsonNode printNthElement(JsonParser jParser, int n) throws IOException {
        int arrayIndex = 0;
        JsonNode element = null;
        while (true) {
            JsonToken token = jParser.nextToken();
            if (token.equals(JsonToken.END_ARRAY)) {
                break;
            } else if (token.equals(JsonToken.START_OBJECT)) {
                arrayIndex++;
                if (arrayIndex == n) {
                    element = jParser.readValueAsTree();
                    break;
                } else {
                    jParser.skipChildren();
                }
            }
        }
        // System.out.println(arrayIndex + " th Element: \n" + element);
        return element;
    }

    /**
     * Get nth element from Jackson Tree
     */
    private JsonNode getElementFromTree(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);
        return root.get("menu").get(root.get("menu").size() - 1);
        // System.out.println(jsonArray.get(0));
    }

    /**
     * Get nth element from Jackson Tree
     */
    private Map getElementFromMap(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map root = mapper.readValue(json, Map.class);
        return (Map) ((List) root.get("menu")).get(0);
        // System.out.println(jsonArray.get(0));
    }
}
