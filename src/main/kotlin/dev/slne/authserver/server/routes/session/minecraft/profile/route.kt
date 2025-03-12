package dev.slne.authserver.server.routes.session.minecraft.profile

import dev.slne.authserver.responses.MinecraftProfilePropertiesResponseImpl
import dev.slne.authserver.types.ProfileActionDTO
import dev.slne.authserver.cache.GameProfileCache
import dev.slne.authserver.serializer.UUIDSerializer
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.profile() {
    get("/{uuid}") {
        val uuid = UUIDSerializer.fromString(call.parameters["uuid"]!!)
        val unsigned = call.queryParameters["unsigned"].toBoolean()

        val result = GameProfileCache.fetchProfile(uuid, unsigned) ?: run {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        val profile = result.profile


        call.respond(
            MinecraftProfilePropertiesResponseImpl(
                id = profile.id,
                name = profile.name,
                properties = profile.properties,
                profileActions = result.actions.map { ProfileActionDTO(it) }.toSet()
            )
        )
    }
}