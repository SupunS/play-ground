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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonReaderTest {

    public static void main(String[] args) throws Exception {
        
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/json-samples/small-sample.json"));
        String json = new String(encoded, "UTF-8");
        String [] requiredElements = new String[]{"//id", "//menu/meta/calories", "//status", "//menu/address"};
        
        while (true) {
            CustomJSONStreamReader customJsonReader = new CustomJSONStreamReader(new StringReader(json), requiredElements);
//             JsonReader customJsonReader = new JsonReader(new StringReader(json));
            // handleElement(customJsonReader);

            JsonParser jp = new JsonParser();
            JsonElement element = jp.parse(customJsonReader);
        }
        
//        System.out.println(element);
        
        /*customJsonReader.beginObject();
        System.out.println(customJsonReader.peek());
            System.out.println(customJsonReader.nextName());
            System.out.println(customJsonReader.peek());
            System.out.println(customJsonReader.nextString());
            System.out.println(customJsonReader.peek());
            System.out.println(customJsonReader.nextName());
            System.out.println(customJsonReader.peek());
            customJsonReader.beginArray();
            System.out.println(customJsonReader.peek());
            
                customJsonReader.beginObject();
                System.out.println(customJsonReader.peek());
                    System.out.println(customJsonReader.nextName());
                    System.out.println(customJsonReader.peek());
                    customJsonReader.beginObject();
                    System.out.println(customJsonReader.peek());
                        System.out.println(customJsonReader.nextName());
                        System.out.println(customJsonReader.peek());
                        System.out.println(customJsonReader.nextInt());
                        System.out.println(customJsonReader.peek());
                    customJsonReader.endObject();
                    System.out.println(customJsonReader.peek());
                customJsonReader.endObject();
                System.out.println(customJsonReader.peek());
                
                customJsonReader.beginObject();
                System.out.println(customJsonReader.peek());
                    System.out.println(customJsonReader.nextName());
                    System.out.println(customJsonReader.peek());
                    customJsonReader.beginObject();
                    System.out.println(customJsonReader.peek());
                        System.out.println(customJsonReader.nextName());
                        System.out.println(customJsonReader.peek());
                        System.out.println(customJsonReader.nextInt());
                        System.out.println(customJsonReader.peek());
                    customJsonReader.endObject();
                    System.out.println(customJsonReader.peek());
                customJsonReader.endObject();
                System.out.println(customJsonReader.peek());
                
                customJsonReader.beginObject();
                System.out.println(customJsonReader.peek());
                    System.out.println(customJsonReader.nextName());
                    System.out.println(customJsonReader.peek());
                    customJsonReader.beginObject();
                    System.out.println(customJsonReader.peek());
                        System.out.println(customJsonReader.nextName());
                        System.out.println(customJsonReader.peek());
                        System.out.println(customJsonReader.nextInt());
                        System.out.println(customJsonReader.peek());
                    customJsonReader.endObject();
                    System.out.println(customJsonReader.peek());
                customJsonReader.endObject();
                System.out.println(customJsonReader.peek());
            customJsonReader.endArray();
            System.out.println(customJsonReader.peek());
            
            System.out.println(customJsonReader.nextName());
            System.out.println(customJsonReader.peek());
            System.out.println(customJsonReader.nextString());
            System.out.println(customJsonReader.peek());
        customJsonReader.endObject();
        System.out.println(customJsonReader.peek());*/
        
        /*customJsonReader.beginObject();
        System.out.println(customJsonReader.peek());*/
    }

    
    private static void handleElement(JsonReader jr) throws IOException {
        while (jr.hasNext()) {
            JsonToken currentToken = jr.peek();
            JsonElement currentElement;
            boolean skip = false;
            String currentElementName;
            switch (currentToken) {
                case BEGIN_OBJECT:
                        jr.beginObject();
                        
                        // Recursively go to children of the current object
                        handleElement(jr);
                        
                        jr.endObject();
                        System.out.println();
                    break;
                case BEGIN_ARRAY:
                        jr.beginArray();
                        
                        handleElement(jr);
                        
                        // End the current array. Remove it from stack
                        jr.endArray();
                        System.out.println();
                    break;
                case NAME:
                    currentElementName = jr.nextName();
                    System.out.print("/" + currentElementName);
                    break;
                case NUMBER:
                    System.out.println();
                    jr.skipValue();
                    break;
                case STRING:
                    System.out.println(" = " + jr.nextString());
                    break;
                case BOOLEAN:
                    System.out.println(" = " + jr.nextBoolean());
                    break;
                case END_DOCUMENT:
                    return;
                default:
                    System.out.println();
                    jr.skipValue();
                    break;
            }
        }
        
    }
}
