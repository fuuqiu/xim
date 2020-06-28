package io.zhudy.xim;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxTest {
    @Test
    public void testBug() {
//        Hooks.onOperatorDebug();
        Flux.range(1, 1)
                .map(i -> i * i)
                .checkpoint("ABC_Test")
                .filter(i -> i % 2 == 0)
                .single()
                .subscribe(System.out::println);
    }

}

