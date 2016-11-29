import noNamespace.PersonsDocument;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestXMLBeans {
    public static void main(String[] args) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(
                    "/home/isurur/Documents/training_ESB/gw-repos/LibTesting/xmlbinding/xmlbeanstesting/src/800kb.xml"));
            String xml = new String(encoded, "UTF-8");

            long start = 0;
            long end = 0;
            for (int i = 0; i < 1000; i++) {
                if (i == 100) {

                    start = System.nanoTime();
                }

                PersonsDocument personsDocument = noNamespace.PersonsDocument.Factory.parse(xml);
//                PersonsDocument.Persons.Person[] persons = personsDocument.getPersons().getPersonArray();
//                for (PersonsDocument.Persons.Person person : persons){
//                   // person.
//                }

            }
            end = System.nanoTime();

            long diff = end - start;
            System.out.println("Time taken " + (diff / 900));
        } catch (XmlException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse XML Document.

    }
}
