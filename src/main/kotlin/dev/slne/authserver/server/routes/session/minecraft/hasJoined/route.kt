package dev.slne.authserver.server.routes.session.minecraft.hasJoined

import dev.slne.authserver.cache.GameProfileCache
import dev.slne.authserver.responses.HasJoinedMinecraftServerResponseImpl
import dev.slne.authserver.server.json
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import org.slf4j.LoggerFactory
import java.net.InetAddress

private val log = LoggerFactory.getLogger("HasJoinedMinecraftServerRoute")

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

        log.info("User $username has joined the server with serverId $serverId")

        val response = HasJoinedMinecraftServerResponseImpl(
            id = profile.id,
            properties = profile.properties,
            profileActions = result.actions
        )

        log.info("Sending response: $response")
        log.info("Sending response: ${json.encodeToString(response)}")

        call.respond(
            response
        )
    }
}