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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GsonTest {
    
    public static void main(String [] args) throws Exception {
        
        GsonTest test = new GsonTest();
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/json-samples/100K-sample.json"));
        String json = new String(encoded, "UTF-8");
        
        
        long startTime = System.currentTimeMillis();
        int count = 0;
        for(int i = 0 ; i < 100000 ; i++) {
            
            // Change this accordingly
            test.printNthElement(json);
            
            count++;
            if ((i % 5000) == 0) {
                long endTime = System.currentTimeMillis();
                double duration = (endTime - startTime);
                System.out.println(count + "/" + duration);
                System.out.println("Single Exection Time: " + duration/count);
                System.out.println("Traversal Rate: " + count/duration*1000);
                startTime = System.currentTimeMillis();
                count = 0;
            }
        }
        
    }
    
    /**
     * print some random element of a json array, with streaming+tree model
     */
    private void printNthElement(String json) throws IOException {
        Reader stringReader = new StringReader(json);
        JsonReader jsonReader = new JsonReader(stringReader);
        
        // consume the first token
        jsonReader.beginObject();
        
        while (jsonReader.hasNext()) {
            jsonReader.skipValue();
            JsonToken token = jsonReader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY)) {
                jsonReader.beginArray();
                
                // Change this accordingly
                printFirstElement(jsonReader);
                break;
            } else {
                jsonReader.skipValue();
            }
        }
        // System.out.println("Done!");
    }
    
    
    /**
     * Read the first element of a array
     */
    private void printFirstElement(JsonReader reader) throws IOException {
        int arrayIndex = 0;
        JsonElement element = null;
        JsonParser jp = new JsonParser();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                element = jp.parse(reader);
                break;
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
            } else
                element = jp.parse(reader);
        }
        // System.out.println(arrayIndex + " th Element: \n" + element);
    }
    
    /**
     * Read last element of a array
     */
    private void printLastElement(JsonReader reader) throws IOException {
        int arrayIndex = 0;
        JsonElement element = null;
        JsonParser jp = new JsonParser();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                element = jp.parse(reader);
                arrayIndex++;
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
            } else
                element = jp.parse(reader);
        }
        // System.out.println(arrayIndex + " th Element: \n" + element);
    }
    
    /**
     * Read last element of a array
     */
    private void printNthElement(JsonReader reader, int n) throws IOException {
        int arrayIndex = 0;
        JsonElement element = null;
        JsonParser jp = new JsonParser();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                arrayIndex++;
                if (arrayIndex == n) {
                    element = jp.parse(reader);
                    break;
                }
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
            } else
                element = jp.parse(reader);
        }
        // System.out.println(arrayIndex + " th Element: \n" + element);
    }
    
    /**
     * Serialize and write a json element to a output stream
     */
    private void serializeJson(JsonElement element, OutputStream outStream) throws IOException {
        Writer writer = new OutputStreamWriter(outStream);
        JsonWriter jsonWriter = new JsonWriter(writer);
        Gson gson = new Gson();
        gson.toJson(element, jsonWriter);
        jsonWriter.flush();
    }
    
    
    private void getElementInMemory(String json) {
        JsonElement root = new JsonParser().parse(json);
        JsonArray jsonArray = root.getAsJsonObject().get("menu").getAsJsonArray();
        JsonElement element = jsonArray.get(0);
        // System.out.println(jsonArray.get(0));
    }
}
