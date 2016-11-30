package xml.tests.jaxb.partial;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class JAXBPartialTest {

    private static String xml;
    private static int n = 40;

    final static QName qName = new QName("person");

    @Setup
    public void setup() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(
                "/home/isurur/Documents/training_ESB/gw-repos/supuns/play-ground/Test/LibTesting/xmlbinding/xmlbeanstesting/src/13kb.xml"));
        xml = new String(encoded, "UTF-8");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(JAXBPartialTest.class.getSimpleName()).warmupIterations(100)
                .measurementIterations(1000).threads(2).forks(1).build();

        new Runner(opt).run();
    }

    @Benchmark
    public void unMarshall() throws IOException {
        try {
            XMLEvent e = null;
            StringReader reader = new StringReader(xml);

            XMLInputFactory xif = XMLInputFactory.newInstance();

            XMLEventReader reader1 = xif.createXMLEventReader(reader);

            JAXBContext jaxbCtx = JAXBContext.newInstance(Persons.class, Person.class);
            Unmarshaller um = jaxbCtx.createUnmarshaller();

            // unmarshall the Example element without parsing the document elements
            Persons example = um.unmarshal(new PartialXMLEventReader(reader1, qName), Persons.class).getValue();
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

        } catch (JAXBException e1) {
            e1.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
