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
package json.tests.reader;

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
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class StackTest {

    ArrayDeque<String> arrayDequeStack = new ArrayDeque<String>();
    StringStack customStack = new StringStack(50);
    
    Stack<String> stack2 = new Stack<String>();
    String[] array;
    
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StackTest.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(20)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
    
    @Setup
    public void setup() throws IOException {
        arrayDequeStack.add("one");
        arrayDequeStack.add("two");
        arrayDequeStack.add("three");
        arrayDequeStack.add("four");
        arrayDequeStack.add("five");
        arrayDequeStack.add("six");
        arrayDequeStack.add("seven");
        arrayDequeStack.add("eight");
        arrayDequeStack.add("nine");
        arrayDequeStack.add("ten");
        
        
        stack2.add("one");
        stack2.add("two");
        stack2.add("three");
        stack2.add("four");
        stack2.add("five");
        stack2.add("six");
        stack2.add("seven");
        stack2.add("eight");
        stack2.add("nine");
        stack2.add("ten");
        
        customStack.push("one");
        customStack.push("one");
        customStack.push("one");
        customStack.push("one");
        customStack.push("one");
        customStack.push("one");
        customStack.push("one");
        customStack.push("one");
        customStack.push("one");
        customStack.push("one");
        
        array = arrayDequeStack.toArray(new String[0]);
    }
    
    @Benchmark
    public void arrayDeque() {
        arrayDequeStack.add("a");
        arrayDequeStack.pollLast();
    }
    
    @Benchmark
    public void stack() {
        stack2.add("a");
        stack2.pop();
    }
    
    @Benchmark
    public void customStack() {
        customStack.push("one");
        customStack.pop();
    }
    
//    @Benchmark
    public void stringBuilderTest() {
        for (int i = 0 ; i < 100 ; i++) {
            StringBuilder sb = new StringBuilder("/");
            for (int j = 0 ; j < 3 ; j++) {
                sb.append("some-text").append("/");
            }
            sb.toString();
        }
    }

//    @Benchmark
    public void stringAppendTest() {
        for (int i = 0 ; i < 100 ; i++) {
            String s = "/";
            for (int j = 0 ; j < 3 ; j++) {
                s = s + "some-text" + "/";
            }
        }
    }
    
    
//    @Benchmark
    public void stringBuilder10Test() {
        for (int i = 0 ; i < 100 ; i++) {
            StringBuilder sb = new StringBuilder("/");
            for (int j = 0 ; j < 15 ; j++) {
                sb.append("some-text").append("/");
            }
            sb.toString();
        }
    }

//    @Benchmark
    public void stringAppend10Test() {
        for (int i = 0 ; i < 100 ; i++) {
            String s = "/";
            for (int j = 0 ; j < 15 ; j++) {
                s = s + "some-text" + "/";
            }
        }
    }
    
    public void arrayDequeLoop() {
        for (int i = 0 ; i < 100 ; i++) {
            for (String s : arrayDequeStack) {
                if(s == "") {
                    // do nothing
                }
            }
        }
    }
    
    public void IteratorLoop() {
        for (int i = 0; i < 100; i++) {
            Iterator<String> itr = arrayDequeStack.iterator();
            while (itr.hasNext()) {
                if (itr.next() == "") {
                    // do nothing
                }
            }
        }
    }
    
    public void customStackLoop() {
        for (int i = 0; i < 100; i++) {
            for (Object s : customStack.toArray()) {
                if(s == "") {
                    // do nothing
                }
            }
        }
    }
    
    public void arrayLoop() {
        for (int i = 0; i < 100; i++) {
            for (String s : array) {
                if(s == "") {
                    // do nothing
                }
            }
        }
    }
}
