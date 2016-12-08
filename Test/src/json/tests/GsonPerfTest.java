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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class GsonPerfTest {

    private static String json;
    private static int n = 40;
    
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(GsonPerfTest.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(20)
                .threads(1)
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
    public JsonElement printFirstElement() throws IOException {
        Reader stringReader = new StringReader(json);
        JsonReader jsonReader = new JsonReader(stringReader);
        JsonParser jp = new JsonParser();
        boolean isInArray = false;
        JsonElement element = null;
        
        // consume the first token
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY)) {
                jsonReader.beginArray();
                isInArray = true;
            } else if (isInArray && token.equals(JsonToken.END_ARRAY)) {
                jsonReader.endArray();
            } else if (isInArray &&  token.equals(JsonToken.BEGIN_OBJECT)) {
                element = jp.parse(jsonReader);
                break;
            } else {
                jsonReader.skipValue();
            }
        }
         return element;
    }

    /**
     * Read last element of a array
     */
    @Benchmark
    public JsonElement printLastElement() throws IOException {
        Reader stringReader = new StringReader(json);
        JsonReader jsonReader = new JsonReader(stringReader);
        JsonParser jp = new JsonParser();
        boolean isInArray = false;
        JsonElement element = null;
        int arrayIndex = 0;
        
        // consume the first token
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY)) {
                jsonReader.beginArray();
                isInArray = true;
            } else if (isInArray && token.equals(JsonToken.END_ARRAY)) {
                jsonReader.endArray();
            } else if (isInArray &&  token.equals(JsonToken.BEGIN_OBJECT)) {
                element = jp.parse(jsonReader);
                arrayIndex++;
            } else {
                jsonReader.skipValue();
            }
        }
        return element;
    }

    /**
     * Read nth element of a array
     */
    public JsonElement printNthElement() throws IOException {
        Reader stringReader = new StringReader(json);
        JsonReader jsonReader = new JsonReader(stringReader);
        JsonParser jp = new JsonParser();
        boolean isInArray = false;
        JsonElement element = null;
        int arrayIndex = 0;
        
        // consume the first token
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY)) {
                jsonReader.beginArray();
                isInArray = true;
            } else if (isInArray && token.equals(JsonToken.END_ARRAY)) {
                jsonReader.endArray();
            } else if (isInArray &&  token.equals(JsonToken.BEGIN_OBJECT)) {
                arrayIndex++;
                if (arrayIndex == n) {
                    element = jp.parse(jsonReader);
                    break;
                } else {
                    jsonReader.skipValue();
                }
            } else {
                jsonReader.skipValue();
            }
        }
        return element;
    }

    /**
     * Serialize and write a json element to a output stream
     */
    public void serializeJson(JsonElement element, OutputStream outStream) throws IOException {
        Writer writer = new OutputStreamWriter(outStream);
        JsonWriter jsonWriter = new JsonWriter(writer);
        Gson gson = new Gson();
        gson.toJson(element, jsonWriter);
        jsonWriter.flush();
    }

    @Benchmark
    public JsonElement getFirstElementInMemory() {
        JsonReader jr = new JsonReader(new StringReader(json));
        JsonElement root = new JsonParser().parse(jr);
        JsonArray jsonArray = root.getAsJsonObject().get("menu").getAsJsonArray();
        return jsonArray.get(0);
    }
    
    @Benchmark
    public JsonElement getLastElementInMemory() {
        JsonReader jr = new JsonReader(new StringReader(json));
        JsonElement root = new JsonParser().parse(jr);
        JsonArray jsonArray = root.getAsJsonObject().get("menu").getAsJsonArray();
        return jsonArray.get(jsonArray.size() - 1);
    }
    
    @Benchmark
    public JsonElement getNthElementInMemory() {
        JsonReader jr = new JsonReader(new StringReader(json));
        JsonElement root = new JsonParser().parse(jr);
        JsonArray jsonArray = root.getAsJsonObject().get("menu").getAsJsonArray();
        return jsonArray.get(n);
    }
}
