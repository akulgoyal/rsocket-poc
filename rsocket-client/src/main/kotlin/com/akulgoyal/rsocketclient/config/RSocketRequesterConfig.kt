package com.akulgoyal.rsocketclient.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import reactor.core.publisher.Mono

@Configuration
class RSocketRequesterConfig {

    @Bean
    fun rsocketRequester(): Mono<RSocketRequester> {
        return RSocketRequester
                .builder()
                .rsocketStrategies(RSocketStrategies.builder()
                        .encoders { it.add(Jackson2CborEncoder()) }
                        .decoders { it.add(Jackson2CborDecoder()) }
                        .build()
                )
                .connectTcp("localhost", 7000)
    }
}
