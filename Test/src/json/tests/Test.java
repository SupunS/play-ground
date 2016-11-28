
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

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

public class Test {

    public static void main(String[] args) throws Exception {
        
        /*Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, "blog/objectgraphs/dynamic/oxm.xml");
        JAXBContext jc = JAXBContext.newInstance("json.tests", Test.class.getClassLoader(), properties);*/
        
        FileInputStream xsdInputStream = new FileInputStream("/home/supun/Desktop/original-deplyment-artifacts/input.xsd");
        DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(xsdInputStream, null, null, null);
        
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        File xml = new File("/home/supun/Desktop/original-deplyment-artifacts/input.xml");
        DynamicEntity customer = (DynamicEntity) unmarshaller.unmarshal(xml);
        
        if (customer.<DynamicEntity>get("food") instanceof List) {
            List<DynamicEntity> foodList = (List<DynamicEntity>)customer.<DynamicEntity>get("food");
            for (int i = 0 ; i < foodList.size() ; i++) {
                System.out.println(foodList.get(i).<DynamicEntity>get("name"));
            }
        }
        
    }
    
    private void AaltoSaxParser() throws Exception {
        InputStream xmlInputStream = new FileInputStream("/home/supun/Desktop/original-deplyment-artofacts/input.xml");
        //Load Aalto's StAX parser factory
        XMLInputFactory2 xmlInputFactory = (XMLInputFactory2) XMLInputFactory.newFactory("javax.xml.stream.XMLInputFactory", XMLInputFactory.class.getClassLoader());
        xmlInputFactory.setProperty(XMLInputFactory2.IS_NAMESPACE_AWARE, true);
        XMLStreamReader2 xmlStreamReader = (XMLStreamReader2) xmlInputFactory.createXMLStreamReader(xmlInputStream);
        
        Map<String, Object> xmlMap = new LinkedHashMap<String, Object>();
        Stack<String> keyStack = new Stack<String>();
        Stack<Object> elementStack = new Stack<Object>();
        String xpath;
        while (xmlStreamReader.hasNext()) {
            int eventType = xmlStreamReader.next();
            switch (eventType) {
                case XMLEvent.START_ELEMENT:
                    keyStack.push(xmlStreamReader.getName().getNamespaceURI());
                    System.out.print("<" + xmlStreamReader.getName().toString() + ">");
                    break;
                case XMLEvent.CHARACTERS:
                    System.out.print(xmlStreamReader.getText());
                    break;
                case XMLEvent.END_ELEMENT:
                    keyStack.pop();
                    System.out.print("</" + xmlStreamReader.getName().toString() + ">");
                    break;
                default:
                    //do nothing
                    break;
            }
        }
        System.err.println("\nDONE.!");
    }
    
    
    private void jdomParser() throws Exception {
        InputStream xmlInputStream = new FileInputStream("/home/supun/Desktop/original-deplyment-artofacts/input.xml");
        SAXBuilder saxbuilder = new SAXBuilder();
        Document jdomDoc = saxbuilder.build(xmlInputStream);
        System.out.println(((Element) jdomDoc.getRootElement().getChildren("food").get(1)).getChildText("name"));
    }
    
    
    private static void javascriptEngine() throws Exception {
        File file = new File("/home/supun/Desktop/original-deplyment-artofacts/input.xml");
        InputStream is = new FileInputStream(file);
        
        DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream("<breakfast_menu><food><name>BelgianWaffles</name><price>$5.95</price><description>TwoofourfamousBelgianWaffleswithplentyofrealmaplesyrup</description><calories>650</calories><orgin>Belgian</orgin><veg>true</veg></food><food><name>StrawberryBelgianWaffles</name><price>$7.95</price><description>LightBelgianwafflescoveredwithstrawberriesandwhippedcream</description><calories>900</calories><orgin>Belgian</orgin><veg>true</veg></food><food><name>Berry-BerryBelgianWaffles</name><price>$8.95</price><description>LightBelgianwafflescoveredwithanassortmentoffreshberriesandwhippedcream</description><calories>900</calories><orgin>Belgian</orgin><veg>true</veg></food><food><name>FrenchToast</name><price>$4.50</price><description>Thickslicesmadefromourhomemadesourdoughbread</description><calories>600</calories><orgin>French</orgin><veg>true</veg></food><food><name>HomestyleBreakfast</name><price>$6.95</price><description>Twoeggs,baconorsausage,toast,andourever-popularhashbrowns</description><calories>950</calories><orgin>French</orgin><veg>false</veg></food></breakfast_menu>".getBytes("UTF-8"));
        
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        
        scriptEngine.eval("var inputbreakfast_menu=\"<breakfast_menu><food><name>BelgianWaffles</name><price>$5.95</price><description>TwoofourfamousBelgianWaffleswithplentyofrealmaplesyrup</description><calories>650</calories><orgin>Belgian</orgin><veg>true</veg></food><food><name>StrawberryBelgianWaffles</name><price>$7.95</price><description>LightBelgianwafflescoveredwithstrawberriesandwhippedcream</description><calories>900</calories><orgin>Belgian</orgin><veg>true</veg></food><food><name>Berry-BerryBelgianWaffles</name><price>$8.95</price><description>LightBelgianwafflescoveredwithanassortmentoffreshberriesandwhippedcream</description><calories>900</calories><orgin>Belgian</orgin><veg>true</veg></food><food><name>FrenchToast</name><price>$4.50</price><description>Thickslicesmadefromourhomemadesourdoughbread</description><calories>600</calories><orgin>French</orgin><veg>true</veg></food><food><name>HomestyleBreakfast</name><price>$6.95</price><description>Twoeggs,baconorsausage,toast,andourever-popularhashbrowns</description><calories>950</calories><orgin>French</orgin><veg>false</veg></food></breakfast_menu>\";" +
            "var is = new java.io.ByteArrayInputStream(inputbreakfast_menu.getBytes(\"UTF-8\"));" +
            "var saxbuilder = new org.jdom.input.SAXBuilder();" +
            "var root = saxbuilder.build(is).getRootElement();");
        System.out.println(scriptEngine.eval("root.getChildren(\"food\").get(0).getChildText(\"name\")"));
    }

}
