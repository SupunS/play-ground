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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class GsonTest {
    
    public static void main(String [] args) throws Exception {
        
        InputStream inStream = new FileInputStream("/home/supun/Desktop/sample.json");
        GsonTest test = new GsonTest();
        test.printNthElement(inStream);
    }
    
    private void printNthElement(InputStream inStream) throws IOException {
        Reader reader = new InputStreamReader(inStream);
        JsonReader jsonReader = new JsonReader(reader);
        
        // consume the first token
        jsonReader.beginObject();
        
        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY)) {
                jsonReader.beginArray();
                handleArray(jsonReader);
            } else {
                jsonReader.skipValue();
            }
        }
        System.out.println("Done!");
    }
    
    private void handleArray(JsonReader reader) throws IOException {
        int arrayIndex = 0;
        JsonElement element = null;
        JsonParser jp = new JsonParser();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                if (arrayIndex == 118079) {
                    element = jp.parse(reader);
                    System.out.println(arrayIndex + " th Element: \n" + element);
                    break;
                } else {
                    reader.skipValue();
                }
                arrayIndex++;
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
            } else
                element = jp.parse(reader);
        }
    }
    
    /**
     * Serialize and write a json element to a output stream
     * 
     * @param element
     * @param outStream
     * @throws IOException
     */
    private void serializeJson(JsonElement element, OutputStream outStream) throws IOException {
        Writer writer = new OutputStreamWriter(outStream);
        JsonWriter jsonWriter = new JsonWriter(writer);
        Gson gson = new Gson();
        gson.toJson(element, jsonWriter);
        jsonWriter.flush();
    }
}
