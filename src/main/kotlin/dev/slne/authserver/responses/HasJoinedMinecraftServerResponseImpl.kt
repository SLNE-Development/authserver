package dev.slne.authserver.responses

import com.mojang.authlib.yggdrasil.ProfileActionType
import dev.slne.authserver.serializer.SerializablePropertyMap
import dev.slne.authserver.serializer.UUIDAsString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HasJoinedMinecraftServerResponseImpl(
    val id: UUIDAsString,
    val properties: SerializablePropertyMap,
    @SerialName("profileActions") val profileActions: Set<ProfileActionType>
)