package dev.slne.authserver.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

val json = Json {
    isLenient = true
    encodeDefaults = true
    ignoreUnknownKeys = true
    prettyPrint = true
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(json)
    }
}
