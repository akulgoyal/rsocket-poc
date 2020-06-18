package com.akulgoyal.rsocketpoc.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class RestApiController {

    @GetMapping("hello")
    fun sayHello(@RequestParam("name") name: String): Mono<String> {
        return Mono.just("Hello $name")
    }
}
