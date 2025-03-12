package fr.loicmathieu.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
//@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS) // lower to 3 x 3s warmup (default 5 x 10s)
//@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS) // lower to 5 x 5s measurement (default 5 x 10s)
public class StringFormatVsJoinerVsBuilderVsConcat {

    private Method method;

    @Setup
    public void setup() throws NoSuchMethodException {
        method = TestClass.class.getMethod("testMethod");
    }

//    @Benchmark
    public String stringFormat() {
        return String.format("%s.%s", method.getDeclaringClass().getName(), method.getName());
    }

//    @Benchmark
    public String stringJoiner() {
        StringJoiner stringJoiner = new StringJoiner(".");
        stringJoiner.add(method.getDeclaringClass().getName());
        stringJoiner.add(method.getName());
        return stringJoiner.toString();
    }

//    @Benchmark
    public String stringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method.getDeclaringClass().getName());
        stringBuilder.append('.');
        stringBuilder.append(method.getName());
        return stringBuilder.toString();
    }

//    @Benchmark
    public String stringBuilderWithCapacity() {
        int capacity = method.getDeclaringClass().getName().length() + method.getName().length() + 1;
        StringBuilder stringBuilder = new StringBuilder(capacity);
        stringBuilder.append(method.getDeclaringClass().getName());
        stringBuilder.append('.');
        stringBuilder.append(method.getName());
        return stringBuilder.toString();
    }

    // improvements proposed by Ladislav (@Ladicek) to avoid multiple reflection call
    @Benchmark
    public String stringBuilderWithCapacityImproved() {
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        int capacity = className.length() + methodName.length() + 1;
        StringBuilder stringBuilder = new StringBuilder(capacity);
        stringBuilder.append(className);
        stringBuilder.append('.');
        stringBuilder.append(methodName);
        return stringBuilder.toString();
    }

    @Benchmark
    public String stringBuilderWithCapacityImprovedReturn() {
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        int capacity = className.length() + methodName.length() + 1;
        StringBuilder stringBuilder = new StringBuilder(capacity)
                .append(className)
                .append('.')
                .append(methodName);
        return stringBuilder.toString();
    }


//    @Benchmark
    public String stringConcat() {
        return method.getDeclaringClass().getName() + '.' + method.getName();
    }

    public static class TestClass {
        public void testMethod() {
            //do nothing here
        }
    }
}
