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

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONElement {
    
    private String name;
    private Object children;
    private JSONDeferredParser parser;
    private JSONElement deferredElement;
    
    JsonReader reader;
    private ArrayDeque<String> elementStack = new ArrayDeque<String>();
    
    /**
     * @param parser
     */
    public JSONElement(String name, JSONDeferredParser parser) {
        this.name = name;
        this.parser = parser;
    }

    
    public Object get(String elementName) {
        if (this.children != null) {
            if (((HashMap<String, Object>) this.children).containsKey(elementName)) {
                return ((JSONElement) this.children).get(elementName);
            } else {
                return parser.read(elementName);
            }
        }
        return this;
    }
    
    
    public void add(String name, Object value) {
        ((HashMap<String, Object>) this.children).put(name, value);
    }
    
    
    public Object read(String elementName) {
        try {
            switch (this.reader.peek()) {
                case STRING:
                    return this.reader.nextString();
                case NUMBER:
                    return this.reader.nextString();
                case BOOLEAN:
                    return this.reader.nextBoolean();
                case NULL:
                    this.reader.nextNull();
                    return null;
                case BEGIN_ARRAY:
                    List<Object> array = new ArrayList<Object>();
                    this.reader.beginArray();
                    while (this.reader.hasNext()) {
                        array.add(read(elementName));
                    }
                    this.reader.endArray();
                    return array;
                case BEGIN_OBJECT:
                    children = new HashMap<String, Object>();
                    this.reader.beginObject();
                        String childName;
                        while (this.reader.hasNext()) {
                            childName = this.reader.nextName();
                            if (elementName.equals(childName)) {
                                return children;
                            } else {
                                ((Map<String, Object>) children).put(childName, read(null));
                            }
                        }
                        this.reader.endObject();
                    return children;
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
    
    
    public String toString() {
        return this.children.toString();
    }
}
