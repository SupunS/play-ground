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
package json.tests.reader;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class CustomJsonReaderTest {
    
    static String currentElementName = "/";
    static Stack<String> readElements = new Stack<String>();
    static Stack<String> elementsStack = new Stack<String>();
    static String [] requiredElements = new String[]{"//id", "//menu/meta/calories"};
    static JsonToken lastToken;

    public static void main(String[] args) throws Exception {
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/json-samples/small-sample.json"));
        String json = new String(encoded, "UTF-8");
        JsonParser jp = new JsonParser();
        JsonReader jr = new JsonReader(new StringReader(json));
        
        
        JSONStreamReader customJsonReader = new JSONStreamReader(new StringReader(json), requiredElements);
        handleElement(customJsonReader, null);
        
        System.out.println(readElements);
    }
    
    /**
     * Recursively traverse through elements in the json object
     * 
     * @param jr
     * @throws IOException
     */
    private static void handleElement(JsonReader jr, JsonElement parent) throws IOException {
        while (jr.hasNext()) {
            JsonToken currentToken = jr.peek();
            JsonElement currentElement;
            boolean skip = false;
            switch (currentToken) {
                case BEGIN_OBJECT:
                    lastToken = JsonToken.BEGIN_OBJECT;
                    if (skip) {
                        jr.skipValue();
                    } else {
                        jr.beginObject();
                        
                        // Add the current object to stack, if its not an anonymous object. 
                        // An anonymous object is an immediate child of an array
                        if (parent != null && parent.getType() != ElementType.ARRAY) {
                            currentElement = new JsonElement(currentElementName, ElementType.OBJECT);
                        } else {
                            currentElement = new JsonElement(null, ElementType.ANONYMOUS);
                        }
                        
                        // Recursively go to children of the current object
                        handleElement(jr, currentElement);
                        
                        // End the current object. Remove it from stack, only if its an named-object (not an anonymous object)
                        jr.endObject();
                        if (!elementsStack.isEmpty() && currentElement.getType() == ElementType.OBJECT) {
                            elementsStack.pop();
                        }
                    }
                    break;
                case BEGIN_ARRAY:
                    lastToken = JsonToken.BEGIN_ARRAY;
                    if (skip) {
                        jr.skipValue();
                    } else {
                        jr.beginArray();
                        
                        // Recursively go to children of the current array
                        currentElement = new JsonElement(currentElementName, ElementType.ARRAY);
                        handleElement(jr, currentElement);
                        
                        // End the current array. Remove it from stack
                        jr.endArray();
                        if (!elementsStack.isEmpty()) {
                            elementsStack.pop();
                        }
                    }
                    break;
                case NAME:
                    currentElementName = jr.nextName();
                    skip = !isRequired(currentElementName);
                    
                    if (skip) {
                        jr.skipValue();
                    } else {
                        elementsStack.add(currentElementName);
                        for (String elementName : elementsStack) {
                            System.out.print(elementName + "/");
                        }
                        System.out.println();
                    }
                    lastToken = JsonToken.NAME;
                    break;
                case NUMBER:
                    if (skip) {
                        jr.skipValue();
                    } else {
                        readElements.add(currentElementName);
                        System.out.println(jr.nextInt());
                    }
                    popPremitive();
                    lastToken = JsonToken.NUMBER;
                    break;
                case STRING:
                    if (skip) {
                        jr.skipValue();
                    } else {
                        jr.nextString();
                        readElements.add(currentElementName);
                    }
                    popPremitive();
                    lastToken = JsonToken.STRING;
                    break;
                case BOOLEAN:
                    if (skip) {
                        jr.skipValue();
                    } else {
                        jr.nextBoolean();
                    }
                    popPremitive();
                    lastToken = JsonToken.BOOLEAN;
                    break;
                case END_OBJECT:
                    jr.endObject();
                    break;
                case END_ARRAY:
                    jr.endArray();
                    break;
                case END_DOCUMENT:
                    return;
                default:
                    jr.skipValue();
                    break;
            }
        }
    }
    
    private static void popPremitive() {
        if (!elementsStack.isEmpty() && lastToken == JsonToken.NAME) {
            elementsStack.pop();
        }
    }
    
    private static boolean isRequired(String element) {
        /*String elementPath = "/";
        for (String elementName : elementsStack) {
            elementPath = elementPath + "/" + elementName;
        }
        elementPath = elementPath + "/" + element;
        
        for(String reqiredElement : requiredElements) {
            if (reqiredElement.startsWith(elementPath)) {
                return true;
            }
        }
        return false;*/
        return true;
    }
}
