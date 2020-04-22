# JMH BENCHMARKS

My personal collection of JMH - Java Microbenchmark Harness - benchmarks.

To know more about JMH, go check its [documentation](https://openjdk.java.net/projects/code-tools/jmh/)
or its [samples](https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples). <br/>
You can also read this [introduction article](http://tutorials.jenkov.com/java-performance/jmh.html) from Jakob Jenkov.

Today it contains only one benchmark, but you can expect to see more of them sooner or later ;)

## How to run it

First, you need to build the benchmark, for this you need to package it via Maven. 

```
mvn clean package
```

It will generate a `benchmarks.jar` file that contains all your benchmarks, and the JMH _infrastructure code_.

Then you can run the jar with the name of the benchmark as first parameter, and optional JMH related parameters.

So something like this command (`-f 1` is to use one fork, which is usually enough):

```
java -jar target/benchmarks.jar <benchmark-name> -f 1
```

Alternatively, you can directly run the benchmark main method via your IDE, but it's not advise, see this quote from JMH website:

> Running benchmarks from the IDE is generally not recommended due to generally uncontrolled environment in which the benchmarks run.

But, running a benchmark is only the easy part of the story, what is hard is to analyze it and understand what it tells us ;).

## ForVsStream benchmark
This benchmark compare the performance of a for loop versus Java Stream.<br/>
You can find the source of this benchmark here: [ForVsStream.java](src/main/java/fr/loicmathieu/jmh/ForVsStream.java)

To run it after having build the benchmark jar, use the following command:

```
java -jar target/benchmarks.jar ForVsStream -f 1
```

If you don't want to run it by yourself, you can find the results [here](run/ForVsStream/results.txt).

I wrote an article (in french) that tries to explain what we saw on this benchmark: [FOR VS STREAM](https://www.loicmathieu.fr/wordpress/informatique/for-vs-stream/)




