package xml.tests.jaxb;

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
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class JAXBTest {

    private static String xml;
    private static int n = 40;

    @Setup
    public void setup() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(
                "/home/isurur/Documents/training_ESB/gw-repos/supuns/play-ground/Test/LibTesting/xmlbinding/xmlbeanstesting/src/13kb.xml"));
        xml = new String(encoded, "UTF-8");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(JAXBTest.class.getSimpleName()).warmupIterations(100)
                .measurementIterations(1000).threads(2).forks(1).build();

        new Runner(opt).run();
    }

    @Benchmark
    public void unMarshall() throws IOException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Persons.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            Persons e = (Persons) jaxbUnmarshaller.unmarshal(reader);
        } catch (JAXBException e1) {
            e1.printStackTrace();
        }
    }

}
