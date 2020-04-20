package fr.loicmathieu.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This benchmarks compare for versus stream performance.
 *
 * To run it, first generate the benchmark jar via <code>mvn clean package</code>.
 * Then launch the benchmark via <code>java -jar target/benchmarks.jar ForVsStream -f 1</code>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS) // lower to 3 x 3s warmup (default 5 x 10s)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS) // lower to 5 x 5s measurement (default 5 x 10s)
public class ForVsStream {

    @Param({"10", "1000", "10000"})
    int size;

    List<Integer> list;

    @Setup
    public void setup() {
        list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
    }

    @Benchmark
    public void testForLoop_doNothing(Blackhole bh) {
        for (Integer i : list) {
            bh.consume(i);
        }
    }

    @Benchmark
    public void testStream_doNothing(Blackhole bh) {
        list.stream().forEach(i -> bh.consume(i));
    }

    @Benchmark
    public int testForLoop_Accumulation() {
        int acc = 0;
        for (Integer i : list) {
            acc += i;
        }
        return acc;
    }

    @Benchmark
    public Integer testStream_AccumulationByMap() {
        return list.stream().mapToInt(Integer::valueOf).sum();
    }

    @Benchmark
    public Integer testStream_AccumulationByReduce() {
        return list.stream().reduce(0, Integer::sum);
    }

    @Benchmark
    public String testForLoop_StringBuilder() {
        StringBuilder builder = new StringBuilder();
        for (Integer i : list) {
            builder.append(i.toString());
        }
        return builder.toString();
    }

    @Benchmark
    public String testStream_StringBuilderByForEach() {
        StringBuilder builder = new StringBuilder();
        list.stream().forEach(i -> builder.append(i.toString()));
        return builder.toString();
    }

    @Benchmark
    public String testStream_StringBuilderByJoining() {
        return list.stream().map(i -> i.toString()).collect(Collectors.joining());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ForVsStream.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
