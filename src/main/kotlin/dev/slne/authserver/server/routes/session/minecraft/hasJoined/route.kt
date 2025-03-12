package dev.slne.authserver.server.routes.session.minecraft.hasJoined

import dev.slne.authserver.cache.GameProfileCache
import dev.slne.authserver.responses.HasJoinedMinecraftServerResponseImpl
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.InetAddress

fun Route.hasJoined() {
    get {
        val params = call.queryParameters

        val username = params["username"]!!
        val serverId = params["serverId"]!!
        val ip = params["ip"]?.let { InetAddress.getByName(it) }

        val result = GameProfileCache.hasJoined(username, serverId, ip) ?: run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        val profile = result.profile

        val response = HasJoinedMinecraftServerResponseImpl(
            id = profile.id,
            properties = profile.properties,
            profileActions = result.actions
        )

        call.respond(
            response
        )
    }
}