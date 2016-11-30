package json.tests.flexjson;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class FlexJson {

    private static String json;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder().include(FlexJson.class.getSimpleName()).warmupIterations(100)
                .measurementIterations(1000).threads(2).forks(1).build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(
                "home/isurur/Documents/training_ESB/gw-repos/supuns/play-ground/Test/src/json/tests/flexjson/100k-sample-json"));
        json = new String(encoded, "UTF-8");
    }

    @Benchmark
    public void deserialize() {
        Food obj = new JSONDeserializer<Food>().deserialize(json, Food.class);
    }

    @Benchmark
    public void serialize() {

        Food stdClass = new Food();
        JSONSerializer serializer = new JSONSerializer().prettyPrint(true);

        //deep serialiing
        String jsonStr = serializer.deepSerialize(stdClass);

        //serializing
        String jsonStr1 = serializer.serialize(stdClass);

    }
}
