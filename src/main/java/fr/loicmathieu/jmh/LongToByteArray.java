package fr.loicmathieu.jmh;

import com.google.common.primitives.Longs;
import org.apache.kafka.common.serialization.LongSerializer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
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
public class LongToByteArray {
    private static final LongSerializer LONG_SERIALIZER = new LongSerializer();

    long timestamp;
    ByteBuffer perThreadBuffer;
    byte result[] = new byte[8];
    
    @Setup
    public void setup() {
        timestamp = System.currentTimeMillis();
        perThreadBuffer = ByteBuffer.allocate(Long.BYTES);
    }

    @Benchmark
    public byte[] testStringValueOf() {
        return String.valueOf(timestamp).getBytes();
    }

    @Benchmark
    public byte[] testGuava() {
        return Longs.toByteArray(timestamp);
    }

    @Benchmark
    public byte[] testKafkaSerde() {
        return LONG_SERIALIZER.serialize(null, timestamp);
    }

    @Benchmark
    public byte[] testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(timestamp);
        return buffer.array();
    }

    @Benchmark
    public byte[] testByteBuffer_reuse() {
        perThreadBuffer.putLong(timestamp);
        byte[] result = perThreadBuffer.array();
        perThreadBuffer.clear();
        return result;
    }

    @Benchmark
    public byte[] testCodingameLike() {
      result[0] = (byte)(timestamp >>> 56 & 0b11111111);
      result[1] = (byte)(timestamp >>> 48 & 0b11111111);
      result[2] = (byte)(timestamp >>> 40 & 0b11111111);
      result[3] = (byte)(timestamp >>> 32 & 0b11111111);
      result[4] = (byte)(timestamp >>> 24 & 0b11111111);
      result[5] = (byte)(timestamp >>> 16 & 0b11111111);
      result[6] = (byte)(timestamp >>> 8  & 0b11111111);
      result[7] = (byte)(timestamp >>> 0  & 0b11111111);
      
      return result;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LongToByteArray.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
