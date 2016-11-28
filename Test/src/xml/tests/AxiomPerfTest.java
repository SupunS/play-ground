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
package xml.tests;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.xml.bind.Element;

public class AxiomPerfTest {

    public static void main(String[] args) throws Exception {

        AxiomPerfTest test = new AxiomPerfTest();
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/xml-samples/800kb.xml"));
        String json = new String(encoded, "UTF-8");

        OMElement nthElement = null;
        long startTime = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; i < 1000000000; i++) {

            // Change this accordingly
            nthElement = test.printFirstElement(json);
            
            count++;
            if ((i % 5000000) == 0) {
                long endTime = System.currentTimeMillis();
                double duration = (endTime - startTime);
                System.out.println(count + "/" + duration);
                System.out.println("Single Exection Time: " + duration / count);
                System.out.println("Traversal Rate: " + count / duration * 1000);
                startTime = System.currentTimeMillis();
                count = 0;
            }
        }
        System.out.println(nthElement);
    }


    /**
     * Read the first element of a array
     */
    private OMElement printFirstElement(String json) throws Exception {
        OMElement root = OMXMLBuilderFactory.createOMBuilder(new StringReader(json)).getDocumentElement();
        return root.getFirstElement();
    }

    /**
     * Read last element of a array
     * 
     * @return
     */
    private OMElement printLastElement(String json) throws Exception {
        OMElement root = OMXMLBuilderFactory.createOMBuilder(new StringReader(json)).getDocumentElement();
        Iterator<OMElement> children = root.getChildElements();
        OMElement child = null;
        while (children.hasNext()) {
            child = children.next();
        }
        return child;
    }

    /**
     * Read last element of a array
     * 
     * @return
     */
    private Element printNthElement(String json, int n) throws IOException {
        return null;
    }
}
