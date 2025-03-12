package dev.slne.authserver

import dev.slne.authserver.server.configureSecurity
import dev.slne.authserver.server.configureSerialization
import dev.slne.authserver.server.routes.configureRouting
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
    embeddedServer(CIO, port = 3002, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureRouting()
}
