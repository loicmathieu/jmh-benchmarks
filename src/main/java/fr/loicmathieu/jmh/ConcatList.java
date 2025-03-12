package fr.loicmathieu.jmh;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 Benchmark                               (size)  Mode  Cnt       Score        Error  Units
 ConcatList.concatWithCommonsCollection      10  avgt    5      57,706 ±     27,376  ns/op
 ConcatList.concatWithCommonsCollection    1000  avgt    5    1631,110 ±    472,323  ns/op
 ConcatList.concatWithCommonsCollection   10000  avgt    5   15434,192 ±   2164,191  ns/op
 ConcatList.concatWithGuava                  10  avgt    5     225,249 ±     83,069  ns/op
 ConcatList.concatWithGuava                1000  avgt    5    7967,360 ±   8693,234  ns/op
 ConcatList.concatWithGuava               10000  avgt    5  108601,706 ± 104439,847  ns/op
 ConcatList.concatWithNewArrayList           10  avgt    5      54,327 ±     10,894  ns/op
 ConcatList.concatWithNewArrayList         1000  avgt    5    1515,667 ±    595,135  ns/op
 ConcatList.concatWithNewArrayList        10000  avgt    5   14827,026 ±   1253,715  ns/op
 ConcatList.concatWithStreamConcat           10  avgt    5     111,837 ±     26,451  ns/op
 ConcatList.concatWithStreamConcat         1000  avgt    5    7104,415 ±   4030,866  ns/op
 ConcatList.concatWithStreamConcat        10000  avgt    5   57587,144 ±  13994,551  ns/op
 ConcatList.concatWithStreamOf               10  avgt    5     157,634 ±     55,757  ns/op
 ConcatList.concatWithStreamOf             1000  avgt    5    5915,797 ±   1692,437  ns/op
 ConcatList.concatWithStreamOf            10000  avgt    5   58156,102 ±   1655,090  ns/op
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS) // lower to 3 x 3s warmup (default 5 x 10s)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS) // lower to 5 x 5s measurement (default 5 x 10s)
public class ConcatList {
    @Param({"10", "1000", "10000"})
    int size;

    List<String> list1;
    List<String> list2;

    @Setup
    public void setup() {
        list1 = new ArrayList<>(size);
        list2 = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list1.add("list1-" + i);
            list2.add("list2-" + i);
        }
    }

    @Benchmark
    public List<String> concatWithStreamConcat() {
        return Stream.concat(list1.stream(), list2.stream())
                .toList();
    }

    @Benchmark
    public List<String> concatWithStreamOf() {
        return Stream.of(list1, list2)
                .flatMap(List::stream)
                .toList();
    }

    @Benchmark
    public List<String> concatWithGuava() {
        return Lists.newArrayList(list1, list2)
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    @Benchmark
    public List<String> concatWithCommonsCollection() {
        return ListUtils.union(list1, list2);
    }

    @Benchmark
    public List<String> concatWithNewArrayList() {
        List<String> newList = new ArrayList<>(list1.size() + list2.size());
        newList.addAll(list1);
        newList.addAll(list2);
        return newList;
    }
}
