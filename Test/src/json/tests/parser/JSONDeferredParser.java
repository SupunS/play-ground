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
package json.tests.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayDeque;

import json.tests.reader.JSONStreamReader;

public class JSONDeferredParser {
    
    JsonReader reader;
    private boolean found = false;
    private JsonElement element;
    private ArrayDeque<String> elementStack = new ArrayDeque<String>();
    
    /**
     * @param customJsonReader
     */
    public JSONDeferredParser() {
    }

    public JSONElement parse(JSONStreamReader reader) {
        return new JSONElement(null, this);
    }
    
    public JsonElement read(String elementName) {
        try {
            switch (this.reader.peek()) {
                case STRING:
                    return new JsonPrimitive(this.reader.nextString());
                case NUMBER:
                    String number = this.reader.nextString();
                    return new JsonPrimitive(new LazilyParsedNumber(number));
                case BOOLEAN:
                    return new JsonPrimitive(this.reader.nextBoolean());
                case NULL:
                    this.reader.nextNull();
                    return JsonNull.INSTANCE;
                case BEGIN_ARRAY:
                    JsonArray array = new JsonArray();
                    this.reader.beginArray();
                    while (this.reader.hasNext()) {
                        array.add(read(elementName));
                    }
                    this.reader.endArray();
                    return array;
                case BEGIN_OBJECT:
                    
                    if (element.isJsonArray()) {
                        element.getAsJsonArray().get(0);
                    }
                    
                    JsonObject object = new JsonObject();
                    this.reader.beginObject();
                        String childName;
                        while (this.reader.hasNext()) {
                            childName = this.reader.nextName();
                            elementStack.add(childName);
                            
                            
                            object.add(childName, read(elementName));
                            if (this.found || elementName.equals(childName)) {
                                return object;
                            }
                        }
                        this.reader.endObject();
                    return object;
                case END_DOCUMENT:
                case NAME:
                case END_OBJECT:
                case END_ARRAY:
                default:
                    throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // we never reach here. Returning null just to make the compiler happy.
        return null;
    }
}
