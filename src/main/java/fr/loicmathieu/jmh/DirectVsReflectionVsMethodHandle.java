package fr.loicmathieu.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS) // lower to 3 x 5s warmup (default 5 x 10s)
@Measurement(iterations = 5, time = 7, timeUnit = TimeUnit.SECONDS) // lower to 5 x 7s measurement (default 5 x 10s)
public class DirectVsReflectionVsMethodHandle {

    private ClassToCall classToCall;
    private Method methodToCall;
    private MethodHandle methodHandleToCall;

    @Setup
    public void setup() throws NoSuchMethodException, IllegalAccessException {
        this.classToCall = new ClassToCall();

        this.methodToCall = classToCall.getClass().getMethod("methodToCall");

        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
        MethodType mt = MethodType.methodType(String.class);
        this.methodHandleToCall = publicLookup.findVirtual(ClassToCall.class, "methodToCall", mt);
    }

    @Benchmark
    public String directCall() {
        return this.classToCall.methodToCall();
    }

    @Benchmark
    public String reflectionCall() throws InvocationTargetException, IllegalAccessException {
        return (String) this.methodToCall.invoke(this.classToCall);
    }

    @Benchmark
    public String methodHandle() throws Throwable {
        return (String) this.methodHandleToCall.invokeExact(this.classToCall);
    }

    public static class ClassToCall {
        public String methodToCall(){
            return "42";
        }
    }
}
