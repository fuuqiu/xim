package io.zhudy.xim.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class Demo {
    public static void main(String[] args) throws JsonProcessingException {
        Flux.just("a", "b")
                .flatMap(
                        item -> {
                            if(item.equals("a")) {
                                return Mono.empty();
                            }
                            return Mono.just(item);
                        })
                .subscribe(System.out::print);
    }
}