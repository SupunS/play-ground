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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class JDOMPerfTest {

    private static String json;
    
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(JDOMPerfTest.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(20)
                .threads(2)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("/home/supun/Desktop/xml-samples/13kb.xml"));
        json = new String(encoded, "UTF-8");
    }

    /**
     * Read the first element of a array
     */
    @Benchmark
    public Element printFirstElement() throws Exception {
        Reader stringReader = new StringReader(json);
        SAXBuilder builder = new SAXBuilder();
        Document root = builder.build(stringReader);
        return root.getRootElement().getChild("person");
    }

    /**
     * Read last element of a array
     */
    @Benchmark
    public Element printLastElement() throws Exception {
        Reader stringReader = new StringReader(json);
        SAXBuilder builder = new SAXBuilder();
        Document root = builder.build(stringReader);
        List<Element> children = root.getRootElement().getChildren();
        return children.get(children.size() -1 );
    }

    /**
     * Read last element of a array
     * 
     * @return
     */
    public Element printNthElement(String json, int n) throws IOException {
        return null;
    }
}
