package com.siha.kotlinexam.config

import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration

@Configuration
class DatabaseConfig: AbstractR2dbcConfiguration() {
    @Bean
    override fun connectionFactory(): ConnectionFactory = H2ConnectionFactory.inMemory("testdb")
}