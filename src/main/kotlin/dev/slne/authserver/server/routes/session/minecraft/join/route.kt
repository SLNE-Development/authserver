package dev.slne.authserver.server.routes.session.minecraft.join

import dev.slne.authserver.requests.JoinMinecraftServerRequestImpl
import dev.slne.authserver.cache.GameProfileCache
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.join() {
    post {
        val request = call.receive<JoinMinecraftServerRequestImpl>()
        GameProfileCache.join(request)
        call.respond(HttpStatusCode.OK)
    }
}