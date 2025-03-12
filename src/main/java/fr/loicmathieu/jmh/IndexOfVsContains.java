package fr.loicmathieu.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/*
Benchmark                                                          (arg)  Mode  Cnt   Score   Error  Units
IndexOfVsContains.contains                    a string that didn't match  avgt    5   7,688 ± 0,634  ns/op
IndexOfVsContains.contains            a string that match at {{the end}}  avgt    5   7,221 ± 0,533  ns/op
IndexOfVsContains.contains            a string that match at {%the end%}  avgt    5   7,281 ± 0,306  ns/op
IndexOfVsContains.contains      {{a}} string that match at the beginning  avgt    5   5,140 ± 0,665  ns/op
IndexOfVsContains.contains          {%a%} string that match at beginning  avgt    5   5,395 ± 0,388  ns/op
IndexOfVsContains.contains         a string that {{match}} at the middle  avgt    5   5,410 ± 0,516  ns/op
IndexOfVsContains.contains         a string that {%match%} at the middle  avgt    5   5,396 ± 0,334  ns/op
IndexOfVsContains.containsBoth                a string that didn't match  avgt    5  12,602 ± 0,792  ns/op
IndexOfVsContains.containsBoth        a string that match at {{the end}}  avgt    5   6,969 ± 0,227  ns/op
IndexOfVsContains.containsBoth        a string that match at {%the end%}  avgt    5  15,979 ± 1,136  ns/op
IndexOfVsContains.containsBoth  {{a}} string that match at the beginning  avgt    5   7,789 ± 0,962  ns/op
IndexOfVsContains.containsBoth      {%a%} string that match at beginning  avgt    5  16,041 ± 0,629  ns/op
IndexOfVsContains.containsBoth     a string that {{match}} at the middle  avgt    5   5,092 ± 0,257  ns/op
IndexOfVsContains.containsBoth     a string that {%match%} at the middle  avgt    5  16,679 ± 1,319  ns/op
IndexOfVsContains.indexOf                     a string that didn't match  avgt    5   6,074 ± 0,953  ns/op
IndexOfVsContains.indexOf             a string that match at {{the end}}  avgt    5   3,406 ± 0,332  ns/op
IndexOfVsContains.indexOf             a string that match at {%the end%}  avgt    5   3,342 ± 0,215  ns/op
IndexOfVsContains.indexOf       {{a}} string that match at the beginning  avgt    5   3,319 ± 0,166  ns/op
IndexOfVsContains.indexOf           {%a%} string that match at beginning  avgt    5   3,346 ± 0,214  ns/op
IndexOfVsContains.indexOf          a string that {{match}} at the middle  avgt    5   3,366 ± 0,270  ns/op
IndexOfVsContains.indexOf          a string that {%match%} at the middle  avgt    5   3,376 ± 0,289  ns/op
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) // lower to 3 x 5s warmup (default 5 x 10s)
@Measurement(iterations = 5, time = 7, timeUnit = TimeUnit.SECONDS) // lower to 5 x 7s measurement (default 5 x 10s)
public class IndexOfVsContains {

    @Param({
        "a string that didn't match",
        "a string that match at {{the end}}",
        "a string that match at {%the end%}",
        "{{a}} string that match at the beginning",
        "{%a%} string that match at beginning",
        "a string that {{match}} at the middle",
        "a string that {%match%} at the middle",
    })
    public String arg;

    @Benchmark
    public boolean indexOf() {
        return arg.indexOf('{') >= 0;
    }

    @Benchmark
    public boolean contains() {
        return arg.contains("{");
    }

    @Benchmark
    public boolean containsBoth() {
        return arg.contains("{{") || arg.contains("%{");
    }
}
