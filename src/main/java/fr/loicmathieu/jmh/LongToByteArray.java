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

    /**
     * long is 64 bits, so need an 8 bytes array
     * 
     * to 'project' the 64 bits into the array of bytes, for each byte in the long (for i=7..0):  
     *    . we shift right the timestamp 8*i bits, 
     *    . mask the result with 8 bits (length of a byte) to clear the remaining bits
     *    . cast and put it in the corresponding index of the byte array
     * 
     * 
     * Exemple : 11001101-01100011-....(remaining 48 bits)
     * result[0] : 
     *   shift 8*7 => 00000000-....-00000000-11001101
     *   mask 0b11111111 =>    ....-00000000-11001101
     *   cast => 11001101
     *  
     * result[1] :
     *   shift 8*6 => 00000000-....-11001101-01100011
     *   mask 0b11111111 =>     ...-00000000-01100011
     *   cast => 01100011
     * 
     * etc..
     * 
     * @return the array of bytes representing the timestamp as a, array of bytes
     */
    @Benchmark
    public byte[] testCodingameStyle() {
      result[0] = (byte)(timestamp >>> 8*7 & 0b11111111);
      result[1] = (byte)(timestamp >>> 8*6 & 0b11111111);
      result[2] = (byte)(timestamp >>> 8*5 & 0b11111111);
      result[3] = (byte)(timestamp >>> 8*4 & 0b11111111);
      result[4] = (byte)(timestamp >>> 8*3 & 0b11111111);
      result[5] = (byte)(timestamp >>> 8*2 & 0b11111111);
      result[6] = (byte)(timestamp >>> 8*1 & 0b11111111);
      result[7] = (byte)(timestamp >>> 8*0 & 0b11111111);
      
      return result;
    }
    
    /*
     * Same as testCodingameStyle, but the mask is removed as the cast do the job implicitly
     */
    @Benchmark
    public byte[] testCodingameStyleWithImplicitMask() {
      result[0] = (byte)(timestamp >>> 8*7);
      result[1] = (byte)(timestamp >>> 8*6);
      result[2] = (byte)(timestamp >>> 8*5);
      result[3] = (byte)(timestamp >>> 8*4);
      result[4] = (byte)(timestamp >>> 8*3);
      result[5] = (byte)(timestamp >>> 8*2);
      result[6] = (byte)(timestamp >>> 8*1);
      result[7] = (byte)(timestamp >>> 8*0);
      
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
