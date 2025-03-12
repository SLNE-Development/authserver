package dev.slne.authserver.responses

import dev.slne.authserver.types.ProfileActionDTO
import dev.slne.authserver.serializer.SerializablePropertyMap
import dev.slne.authserver.serializer.UUIDAsString
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftProfilePropertiesResponseImpl(
    val id: UUIDAsString,
    val name: String,
    val properties: SerializablePropertyMap,
    val profileActions: Set<ProfileActionDTO>?
)