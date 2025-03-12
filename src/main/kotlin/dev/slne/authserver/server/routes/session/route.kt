package dev.slne.authserver.server.routes.session

import dev.slne.authserver.server.routes.session.minecraft.minecraft
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.session() {
    route("minecraft") { minecraft() }
}