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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class JacksonXmlTest {
    
    public static void main(String [] args) throws Exception {
        
        // Read the json and bind to objects
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/original-deplyment-artifacts/output.xml"));
        String xml = new String(encoded, "UTF-8");
        
        XMLInputFactory f = XMLInputFactory.newFactory();
        XMLStreamReader sr = f.createXMLStreamReader(new FileInputStream("/home/supun/Desktop/original-deplyment-artifacts/output.xml"));
        
        XmlMapper xmlMapper = new XmlMapper();
        while( sr.hasNext()) {
            sr.next(); // to point to <root>
            if (sr.isStartElement()) {
                if ("item".equals(sr.getLocalName())) {
                    Map root = xmlMapper.readValue(sr, Map.class);
                    System.out.println(root);
                }
            }
        }
//        sr.next(); // to point to root-element under root
        
    }
}
