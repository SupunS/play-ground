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

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * @author supun
 *
 */
public class MyEntityResolver implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        // Imported schemas are located in ext\appdata\xsd\

         // Grab only the filename part from the full path
        String filename = new File(systemId).getName();

        // Now prepend the correct path
        String correctedId = "ext/appdata/xsd/" + filename;

        InputSource is = new InputSource(ClassLoader.getSystemResourceAsStream(correctedId));
        is.setSystemId(correctedId);

        return is;
     }
}
