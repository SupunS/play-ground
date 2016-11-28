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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JsonTraverseTest {
    
    public static void main(String [] args) throws Exception {
        
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/original-deplyment-artifacts/output.json"));
        String json = new String(encoded, "UTF-8");
        
        long startTime = System.currentTimeMillis();
        int count = 0;
        for(int i = 0 ; i < 10000000 ; i++) {
            traverseWithGson(json);
            count++;
            if ((i % 50000) == 0) {
                long endTime = System.currentTimeMillis();
                double duration = (endTime - startTime);
                System.out.println(count + "/" + duration);
                System.err.println("Single Exection Time: " + duration/count);
                System.err.println("Traversal Rate: " + count/duration*1000);
                startTime = System.currentTimeMillis();
                count = 0;
            }
        }
    }

    
    /* JSON Parsing with Jackson POJO model */
    
    private static void traverseWithObjectModel(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,?> root = mapper.readValue(json, Map.class);
        printElements(null, root);
    }
    
    private static void printElements(String fieldName, Object element) {
        if (element instanceof List) {
            List<?> elementArray = (List<?>) element;
            for (int i = 0; i < elementArray.size(); i++) {
                printElements(fieldName, elementArray.get(i));
            }
        } else if (element instanceof Map) {
            Map<String, ?> elementsMap = (Map<String, ?>) element;
            for (Entry<String, ?> child: elementsMap.entrySet()) {
                printElements(child.getKey(), child.getValue());
            }
        } else {
            String key = fieldName; 
            Object value = element;
//            System.out.println(fieldName + " - " + element);
        }
    }
    
    
    /* JSON parsing with Jackson Tree model */
    
    private static void traverseWithTreeModel(String json) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);
        printTreeElements(null, root);
    }

    private static void printTreeElements(String fieldName, JsonNode element) {
        if (element.isArray()) {
            for (JsonNode child : element) {
                printTreeElements(fieldName, child);
            }
        } else if (element.isObject()) {
            Iterator<String> fieldNames = element.fieldNames();
            while (fieldNames.hasNext()) {
                String childName = fieldNames.next();
                printTreeElements(childName, element.get(childName));
            }
        } else {
            String key = fieldName; 
            String value = element.asText();
//            System.out.println(fieldName + " - " + element.asText());
        }
    }
    
    
    /* org.json */
    private static void traverseWithJavaJson(String json) throws Exception {
        JSONObject root = new JSONObject(json);
        printJavaJsonElements(null, root);
    }

    private static void printJavaJsonElements(String fieldName, Object element) throws Exception {
        if (element instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) element;
            for (int i = 0; i < jsonArray.length(); i++) {
                printJavaJsonElements(fieldName, jsonArray.get(i));
            }
        } else if (element instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) element;
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                printJavaJsonElements(key, jsonObject.get(key));
            }
        } else {
            String key = fieldName; 
            Object value = element;
//            System.out.println(fieldName + " - " + element);
        }
    }
    
    
    /* gson */
    private static void traverseWithGson(String json) throws Exception {
        JsonElement root = new JsonParser().parse(json);
        printGsonElements(null, root);
    }

    private static void printGsonElements(String fieldName, JsonElement element) throws Exception {
        if (element instanceof JsonArray) {
            JsonArray jsonArray = element.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                printGsonElements(fieldName, jsonArray.get(i));
            }
        } else if (element instanceof JsonObject) {
            JsonObject jsonObject = element.getAsJsonObject();
            for (Entry<String,JsonElement> child : jsonObject.entrySet()) {
                printGsonElements(child.getKey(), child.getValue());
            }
        } else {
            String key = fieldName; 
            Object value = element;
//            System.out.println(fieldName + " - " + element);
        }
    }
    
}
