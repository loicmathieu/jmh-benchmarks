package fr.loicmathieu.jmh;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS) // lower to 3 x 3s warmup (default 5 x 10s)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS) // lower to 5 x 5s measurement (default 5 x 10s)
public class ConcatStream {
    @Param({"10", "100", "1000"})
    int size;

    List<String> list1;
    List<String> list2;
    List<String> list3;

    @Setup
    public void setup() {
        list1 = new ArrayList<>(size);
        list2 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list1.add("list1-" + i);
            list2.add("list2-" + i);
        }

        // always use a small list3 with 5 elements
        list3 = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            list1.add("list3-" + i);
        }
    }

    @Benchmark
    public List<String> concatWithStreamConcat_empty() {
        return Stream.concat(list1.stream(), Stream.concat(Stream.empty(), Stream.empty())).toList();
    }

    @Benchmark
    public List<String> concatWithStreamOf_empty() {
        return Stream.of(list1.stream(), Stream.<String>empty(), Stream.<String>empty())
                .flatMap(Function.identity())
                .toList();
    }

    @Benchmark
    public List<String> concatWithStreamConcat_filled() {
        return Stream.concat(list1.stream(), Stream.concat(list2.stream(), list3.stream())).toList();
    }

    @Benchmark
    public List<String> concatWithStreamOf_filled() {
        return Stream.of(list1.stream(), list2.stream(), list3.stream())
                .flatMap(Function.identity())
                .toList();
    }
}
