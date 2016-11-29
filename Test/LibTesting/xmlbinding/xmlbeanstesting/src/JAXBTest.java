import noNamespace.PersonsDocument;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by isurur on 11/24/16.
 */
public class JAXBTest {
    public static void main(String[] args) {
        JAXBContext jaxbContext = null;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(
                    "/home/isurur/Documents/training_ESB/gw-repos/LibTesting/xmlbinding/xmlbeanstesting/src/13kb.xml"));
            String xml = new String(encoded, "UTF-8");

            jaxbContext = JAXBContext.newInstance(Persons.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            long start = 0;
            long end = 0;
            for (int i = 0; i < 1000; i++) {
                if (i == 100) {

                    start = System.nanoTime();
                }

                StringReader reader = new StringReader(xml);
                Persons e = (Persons) jaxbUnmarshaller.unmarshal(reader);
            }
            end = System.nanoTime();

            long diff = end - start;
            System.out.println("Time taken " + (diff / 900));

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
