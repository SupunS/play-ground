package partial;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by isurur on 11/28/16.
 */
public class JAXBPartialTest {

    final static QName qName = new QName("person");

    public static void main(String[] args) {

        try {
            byte[] encoded = Files.readAllBytes(Paths.get(
                    "/home/isurur/Documents/training_ESB/gw-repos/LibTesting/xmlbinding/xmlbeanstesting/src/13kb.xml"));
            String xml = new String(encoded, "UTF-8");


//            // unmarshall the Example element without parsing the document elements
//            Persons example = um.unmarshal(new PartialXMLEventReader(reader1, qName), Persons.class).getValue();
//
//            Long docId = 0l;
                 XMLEvent e = null;
            XMLInputFactory xif = XMLInputFactory.newInstance();

            // loop though the xml stream

            long start = 0;
            long end = 0;
            for (int i = 0; i < 1000; i++) {
                StringReader reader = new StringReader(xml);


                XMLEventReader reader1 = xif.createXMLEventReader(reader);

                JAXBContext jaxbCtx = JAXBContext.newInstance(Persons.class, Person.class);
                Unmarshaller um = jaxbCtx.createUnmarshaller();

                // unmarshall the Example element without parsing the document elements
                Persons example = um.unmarshal(new PartialXMLEventReader(reader1, qName), Persons.class).getValue();
                if (i == 100) {

                    start = System.nanoTime();
                }
                int count = 0;
                while ((e = reader1.peek()) != null && count == 0) {
                    count++;
                    // check the event is a Document start element
                    if (e.isStartElement() && ((StartElement) e).getName().equals(qName)) {

                        // unmarshall the document
                        Person document = um.unmarshal(reader1, Person.class).getValue();

                    } else {
                        reader1.next();
                    }

                }


            }
            end = System.nanoTime();

            long diff = end - start;
            System.out.println("Time taken " + (diff / 900));
        } catch (XMLStreamException e1) {

        } catch (JAXBException e1) {

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
