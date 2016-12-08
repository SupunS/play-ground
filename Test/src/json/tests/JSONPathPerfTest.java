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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.internal.path.CompiledPath;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerina.model.types.JSONType;
import org.wso2.ballerina.runtime.datatypes.json.JSONUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class JSONPathPerfTest {
    
    static String jsonString;
    static  JsonElement gsonJson;
    static JSONType json1;
    static JSONType json2;

    private static final Logger logger = LoggerFactory.getLogger(CompiledPath.class);
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(JSONPathPerfTest.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(20)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
    
    @Setup
    public void setup() throws Exception {
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/json-samples/small-sample.json"));
        jsonString = new String(encoded, "UTF-8");
        json1 = new JSONType(jsonString);
        json2 = new JSONType(jsonString);
    }
    
    @Benchmark
    public void jsonPathGet() {
        JSONUtils utils = new JSONUtils();
        utils.get(json1, "$.id");
        utils.get(json1, "$.menu[2].meta.calories");
    }
    
    @Benchmark
    public void objectGet() {
        json2.getDocument().getAsJsonObject().get("id");
        json2.getDocument().getAsJsonObject().get("menu").getAsJsonArray().get(2).getAsJsonObject().get("meta").getAsJsonObject().get("calories");
    }
    
    @Benchmark
    public void jsonPathSet() {
        JSONUtils utils = new JSONUtils();
        utils.set(json1, "$.id", "new-id");
        utils.set(json1, "$.menu[2].meta.calories", 999);
    }
    
    @Benchmark
    public void objectSet() {
        json2.getDocument().getAsJsonObject().remove("id");
        json2.getDocument().getAsJsonObject().add("id", new JsonPrimitive("new-id"));
        json2.getDocument().getAsJsonObject().get("menu").getAsJsonArray().get(2).getAsJsonObject().get("meta").getAsJsonObject().remove("calories");
        json2.getDocument().getAsJsonObject().get("menu").getAsJsonArray().get(2).getAsJsonObject().get("meta").getAsJsonObject().add("calories", new JsonPrimitive(999));
    }
}
