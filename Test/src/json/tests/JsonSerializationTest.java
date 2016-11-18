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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonSerializationTest {
    
    public static void main(String [] args) throws Exception {
        
        // Read the json and bind to objects
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/original-deplyment-artifacts/output.json"));
        String json = new String(encoded, "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(json, JsonNode.class);
        
        // Write the object back to output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, root);
        System.out.println(new String(out.toByteArray(),"UTF-8"));
    }
}
