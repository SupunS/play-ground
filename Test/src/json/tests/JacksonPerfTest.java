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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class JacksonPerfTest {

    private static String json;
    private static int n = 40;
    
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(JacksonPerfTest.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(20)
                .threads(2)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
    
    @Setup
    public void setup() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/json-samples/1M-sample.json"));
        json = new String(encoded, "UTF-8");
    }

    /**
     * Read the first element of a array
     */
    @Benchmark
    public JsonNode printFirstElement() throws IOException {
        JsonFactory jfactory = new JsonFactory();
        jfactory.setCodec(new ObjectMapper());
        JsonParser jParser = jfactory.createParser(json);
        JsonNode element = null;
        
        boolean isInArray = false;

        while (true) {
            JsonToken token = jParser.nextToken();
            if (token == null) {
                break;
            } else if (token.equals(JsonToken.START_ARRAY)) {
                isInArray = true;
            } else if (isInArray && token.equals(JsonToken.END_ARRAY)) {
                break;
            } else if (isInArray &&  token.equals(JsonToken.START_OBJECT)) {
                element = jParser.readValueAsTree();
                break;
            }
        }
        return element;
    }

    /**
     * Read last element of a array
     */
    @Benchmark
    public JsonNode printNthElement() throws IOException {
        JsonFactory jfactory = new JsonFactory();
        jfactory.setCodec(new ObjectMapper());
        JsonParser jParser = jfactory.createParser(json);
        JsonNode element = null;
        int arrayIndex = 0;
        boolean isInArray = false;

        while (true) {
            JsonToken token = jParser.nextToken();
            if (token == null) {
                break;
            } else if (token.equals(JsonToken.START_ARRAY)) {
                isInArray = true;
            } else if (isInArray && token.equals(JsonToken.END_ARRAY)) {
                break;
            } else if (isInArray &&  token.equals(JsonToken.START_OBJECT)) {
                arrayIndex ++;
                if (arrayIndex == n) {
                    element = jParser.readValueAsTree();
                    break;
                } else {
                    jParser.skipChildren();
                }
            }
        }
        return element;
    }
    
    /**
     * Read last element of a array
     */
    @Benchmark
    public JsonNode printLastElement() throws IOException {
        JsonFactory jfactory = new JsonFactory();
        jfactory.setCodec(new ObjectMapper());
        JsonParser jParser = jfactory.createParser(json);
        JsonNode element = null;
        int arrayIndex = 0;
        boolean isInArray = false;

        while (true) {
            JsonToken token = jParser.nextToken();
            if (token == null) {
                break;
            } else if (token.equals(JsonToken.START_ARRAY)) {
                isInArray = true;
            } else if (isInArray && token.equals(JsonToken.END_ARRAY)) {
                break;
            } else if (isInArray &&  token.equals(JsonToken.START_OBJECT)) {
                arrayIndex ++;
                element = jParser.readValueAsTree();
            }
        }
        return element;
    }


    /**
     * Get first element from Jackson Tree
     */
    @Benchmark 
    public JsonNode getFirstElementFromTree() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);
        return root.get("menu").get(0);
    }

    /**
     * Get first element from Jackson Map
     */
    @Benchmark 
    public Map getFirstElementFromMap() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map root = mapper.readValue(json, Map.class);
        return (Map) ((List) root.get("menu")).get(0);
    }
    
    
    /**
     * Get nth element from Jackson Tree
     */
    @Benchmark 
    public JsonNode getNthElementFromTree() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);
        return root.get("menu").get(n);
    }

    /**
     * Get nth element from Jackson Map
     */
    @Benchmark 
    public Map getNthElementFromMap() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map root = mapper.readValue(json, Map.class);
        return (Map) ((List) root.get("menu")).get(n);
    }
    
    
    /**
     * Get last element from Jackson Tree
     */
    @Benchmark 
    public JsonNode getLastElementFromTree() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);
        JsonNode menu = root.get("menu");
        return menu.get(menu.size() - 1);
    }

    /**
     * Get last element from Jackson Map
     */
    @Benchmark 
    public Map getLastElementFromMap() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map root = mapper.readValue(json, Map.class);
        List menu = (List) root.get("menu");
        return (Map) menu.get(menu.size() -1);
    }
}
