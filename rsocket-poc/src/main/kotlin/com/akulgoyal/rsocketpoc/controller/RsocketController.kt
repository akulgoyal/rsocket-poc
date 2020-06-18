package com.akulgoyal.rsocketpoc.controller

import com.akulgoyal.rsocketpoc.utils.logger
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList


@Controller
class RsocketController {

    private val log = logger()

    private val messages = CopyOnWriteArrayList<String>()

    // Request - Stream communication
    @MessageMapping("getNumbers")
    fun getNumbers(@RequestBody message: String): Flux<String> {
        log.info(message)
        return Flux.range(0, 1000)
                .map { "$it\n" }
    }

    // Request - Response communication
    @MessageMapping("play")
    fun play(@RequestBody message: Mono<String>): Mono<String> {
        return message
                .map { "$it pong" }
    }

    // Fire and forget communication
    @MessageMapping("store")
    fun store(@RequestBody payload: Mono<String>): Mono<Void> {
        return payload
                .map { messages.add(it) }
                .doOnNext { log.info("Stored values: $messages") }
                .then()
    }

    // Bi-directional channel communication
    @MessageMapping("channel")
    fun channel(settings: Flux<Duration>): Flux<Long> {
        return settings
                .doOnNext { log.info("\nFrequency setting is ${it.seconds} second(s).") }
                .switchMap { setting -> Flux.interval(setting).map { it } }
                .log()
    }
}
