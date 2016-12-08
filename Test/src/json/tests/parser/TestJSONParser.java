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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import json.tests.reader.JSONStreamReader;

public class TestJSONParser {

    public static void main(String[] args) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/json-samples/small-sample.json"));
        String json = new String(encoded, "UTF-8");
        String[] requiredElements = new String[] { "//id", "//menu/meta/calories", "//status" };

        /*JSONStreamReader customJsonReader = new JSONStreamReader(new StringReader(json), requiredElements);
        JSONDeferredParser parser = new JSONDeferredParser();
        JSONElement root = parser.parse(customJsonReader);
        System.out.println(root.get("id"));
        System.out.println(root.get("id"));*/
        
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(json);
        System.out.println(e.getAsJsonObject().get("xxx"));
        
    }

}
