package dev.slne.authserver.service

import com.mojang.authlib.yggdrasil.ProfileResult
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import dev.slne.authserver.responses.HasJoinedMinecraftServerResponseImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.Proxy
import java.util.*

object MojangApi {
    private val authenticationService = YggdrasilAuthenticationService(Proxy.NO_PROXY)
    private val sessionService = authenticationService.createMinecraftSessionService()
//    private val gameProfileRepository = authenticationService.createProfileRepository()

    suspend fun hasJoined(
        name: String,
        serverId: String,
        address: InetAddress?
    ): HasJoinedMinecraftServerResponseImpl? = withContext(Dispatchers.IO) {
        val profile = sessionService.hasJoinedServer(name, serverId, address)
        if (profile == null) {
            return@withContext null
        }

        HasJoinedMinecraftServerResponseImpl(
            id = profile.profile.id,
            properties = profile.profile.properties,
            profileActions = profile.actions
        )
    }

    suspend fun fetchProfile(
        uuid: UUID,
        unsigned: Boolean
    ): ProfileResult? = withContext(Dispatchers.IO) {
        sessionService.fetchProfile(uuid, !unsigned)
    }
}