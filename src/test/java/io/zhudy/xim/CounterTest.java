package io.zhudy.xim;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

/**
 * 计数器
 */
public class CounterTest {
    private CounterTest() {
        Metrics.counter("objects.instance").increment(1.0);
    }

    @Test
    public void givenGlobalRegistry_whenIncrementAnywhere_thenCounted() {
        Metrics.addRegistry(new SimpleMeterRegistry());
        Metrics.counter("objects.instance").increment();
        CounterTest cc = new CounterTest();
        System.out.println(cc);
        Counter counter = Metrics.globalRegistry
                .find("objects.instance").counter();
        assertTrue(counter.count() == 2.0);
    }
}
