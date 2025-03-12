package dev.slne.authserver.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.mojang.authlib.GameProfile
import com.mojang.authlib.yggdrasil.ProfileResult
import com.sksamuel.aedile.core.asCache
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.authserver.requests.JoinMinecraftServerRequestImpl
import dev.slne.authserver.service.MojangApi
import java.net.InetAddress
import java.util.*
import kotlin.time.Duration.Companion.seconds

object GameProfileCache {
    private val hasJoinedCache = Caffeine.newBuilder()
        .expireAfterWrite(10.seconds)
        .asCache<String, ProfileResult>()

    private val nameProfileCache = Caffeine.newBuilder()
        .expireAfterWrite(10.seconds)
        .asCache<String, ProfileResult>()

    private val uuidProfileCache = Caffeine.newBuilder()
        .expireAfterWrite(10.seconds)
        .asCache<UUID, ProfileResult>()


    suspend fun fetchProfile(uuid: UUID, unsigned: Boolean): ProfileResult? {
        val cachedResult = uuidProfileCache.getIfPresent(uuid)
        if (cachedResult != null) {
            return cachedResult
        }

        return MojangApi.fetchProfile(uuid, unsigned)
    }

    suspend fun join(request: JoinMinecraftServerRequestImpl) {
        val profile = fetchProfile(request.selectedProfile, true)
        if (profile != null) {
            hasJoinedCache.put(request.serverId, profile)
        }
    }

    suspend fun hasJoined(username: String, serverId: String, address: InetAddress?): ProfileResult? {
        val cacheResult = hasJoinedCache.getIfPresent(serverId)
        if (cacheResult != null) {
            return cacheResult
        }

        val mojangResult = MojangApi.hasJoined(username, serverId, address) ?: return null
        val profile = GameProfile(mojangResult.id, username).apply {
            properties.putAll(mojangResult.properties)
        }
        val result = ProfileResult(profile, mojangResult.profileActions)

        nameProfileCache.put(username, result)
        uuidProfileCache.put(mojangResult.id, result)

        return result
    }
}