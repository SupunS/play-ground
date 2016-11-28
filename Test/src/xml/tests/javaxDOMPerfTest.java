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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class javaxDOMPerfTest {

    public static void main(String[] args) throws Exception {

        javaxDOMPerfTest test = new javaxDOMPerfTest();
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/xml-samples/800kb.xml"));
        String json = new String(encoded, "UTF-8");

        Node nthElement = null;
        long startTime = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; i < 100000; i++) {

            // Change this accordingly
            nthElement = test.printLastElement(json);
            
            count++;
            if ((i % 5000) == 0) {
                long endTime = System.currentTimeMillis();
                double duration = (endTime - startTime);
                System.out.println(count + "/" + duration);
                System.out.println("Single Exection Time: " + duration / count);
                System.out.println("Traversal Rate: " + count / duration * 1000);
                startTime = System.currentTimeMillis();
                count = 0;
            }
        }
        Document document = nthElement.getOwnerDocument();
        DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        String str = serializer.writeToString(nthElement);
        System.out.println(str);
    }


    /**
     * Read the first element of a array
     */
    private Node printFirstElement(String json) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(json));
        Document root = dBuilder.parse(is);
        return root.getFirstChild().getChildNodes().item(0);
    }

    /**
     * Read last element of a array
     * 
     * @return
     */
    private Node printLastElement(String json) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(json));
        Document root = dBuilder.parse(is);
        NodeList children = root.getElementsByTagName("person");
        return children.item(children.getLength() -1 );
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
