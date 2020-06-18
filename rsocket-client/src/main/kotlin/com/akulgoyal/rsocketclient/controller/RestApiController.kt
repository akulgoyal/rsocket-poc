package com.akulgoyal.rsocketclient.controller

import com.akulgoyal.rsocketclient.utils.logger
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.retrieveFlux
import org.springframework.messaging.rsocket.retrieveMono
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.math.roundToInt

@RestController
class RestApiController {

    private val log = logger()

    private val rsocketRequester = RSocketRequester
            .builder()
            .rsocketStrategies(RSocketStrategies.builder()
                    .encoders { it.add(Jackson2CborEncoder()) }
                    .decoders { it.add(Jackson2CborDecoder()) }
                    .build()
            )
            .connectTcp("localhost", 7000)

    @GetMapping("request-stream")
    fun test(): Flux<String> {
        return getNumbersFromServer()
    }

    @GetMapping("request-response")
    fun test2(): Mono<String> {
        return rsocketRequester
                .flatMap { it
                        .route("play")
                        .data("ping")
                        .retrieveMono<String>()
                }
    }

    @GetMapping("fire-and-forget")
    fun test3(): Mono<String> {
        return getNumbersFromServer()
                .elementAt((Math.random() * 1000).roundToInt())
                .flatMap { storeDataOnServer(it) }
                .thenReturn("It's done!")
    }

    @GetMapping("channel")
    fun test4(): Mono<Void> {
        val setting1 = Mono.just(Duration.ofSeconds(1))
        val setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5))
        val setting3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15))
        val settings = Flux.concat(setting1, setting2, setting3)

        rsocketRequester
                .flatMapMany {
                    it.route("channel")
                            .data(settings)
                            .retrieveFlux<Long>()
                }
                .takeUntil { it.toInt() == 7 }
                .subscribe { log.info(it.toString()) }

        return Mono.empty()
    }

    private fun storeDataOnServer(data: String): Mono<Void> {
        return rsocketRequester
                .flatMap {
                    it.route("store")
                            .data(data)
                            .retrieveMono<Void>()
                }
    }

    private fun getNumbersFromServer(): Flux<String> {
        return rsocketRequester
                .flatMapMany {
                    it
                            .route("getNumbers")
                            .data("Client says Hi!")
                            .retrieveFlux<String>()
                }
    }
}
