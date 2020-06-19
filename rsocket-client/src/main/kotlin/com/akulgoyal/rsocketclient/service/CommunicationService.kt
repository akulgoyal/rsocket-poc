package com.akulgoyal.rsocketclient.service

import com.akulgoyal.rsocketclient.utils.logger
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveFlux
import org.springframework.messaging.rsocket.retrieveMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.math.roundToInt

@Service
class CommunicationService(
        val rsocketRequester: Mono<RSocketRequester>
) {

    private val log = logger()

    fun requestStream(): Flux<String> = getNumbersFromServer()

    fun requestResponse(): Mono<String> {
        return rsocketRequester
                .flatMap { it
                        .route("play")
                        .data("ping")
                        .retrieveMono<String>()
                }
    }

    fun fireAndForget(): Mono<String> {
        return getNumbersFromServer()
                .elementAt((Math.random() * 1000).roundToInt())
                .flatMap { storeDataOnServer(it) }
                .thenReturn("It's done!")
    }

    fun initiateBiDirectionalChannel(): Mono<Void> {
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
