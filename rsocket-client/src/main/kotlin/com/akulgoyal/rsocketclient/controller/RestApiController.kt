package com.akulgoyal.rsocketclient.controller

import com.akulgoyal.rsocketclient.service.CommunicationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class RestApiController(
        val communicationService: CommunicationService
) {

    @GetMapping("request-stream")
    fun requestStream(): Flux<String> {
        return communicationService.requestStream()
    }

    @GetMapping("request-response")
    fun requestResponse(): Mono<String> {
        return communicationService.requestResponse()
    }

    @GetMapping("fire-and-forget")
    fun fireAndForget(): Mono<String> {
        return communicationService.fireAndForget()
    }

    @GetMapping("channel")
    fun channel(): Mono<Void> {
        return communicationService.initiateBiDirectionalChannel()
    }
}
