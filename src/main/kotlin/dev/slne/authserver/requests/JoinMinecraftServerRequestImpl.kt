package dev.slne.authserver.requests

import dev.slne.authserver.serializer.UUIDAsString
import kotlinx.serialization.Serializable

@Serializable
data class JoinMinecraftServerRequestImpl(
    val accessToken: String,
    val selectedProfile: UUIDAsString,
    val serverId: String
)