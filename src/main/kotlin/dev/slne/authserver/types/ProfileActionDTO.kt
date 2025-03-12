package dev.slne.authserver.types

import com.mojang.authlib.yggdrasil.ProfileActionType
import com.mojang.authlib.yggdrasil.response.ProfileAction
import kotlinx.serialization.Serializable

@Serializable
data class ProfileActionDTO(
    val action: ProfileActionType,
)

fun ProfileAction.toDTO() = ProfileActionDTO(type)
fun ProfileActionDTO.toModel() = ProfileAction(action)

fun Set<ProfileAction>.toDTO() = map { it.toDTO() }.toSet()