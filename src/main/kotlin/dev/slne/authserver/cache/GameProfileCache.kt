package dev.slne.authserver.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.mojang.authlib.GameProfile
import com.mojang.authlib.yggdrasil.ProfileResult
import com.sksamuel.aedile.core.asCache
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.authserver.requests.JoinMinecraftServerRequestImpl
import dev.slne.authserver.responses.HasJoinedMinecraftServerResponseImpl
import dev.slne.authserver.service.MojangApi
import java.net.InetAddress
import java.util.*
import kotlin.time.Duration.Companion.minutes

object GameProfileCache {
    private val hasJoinedCache = Caffeine.newBuilder()
        .expireAfterWrite(10.minutes)
        .withRemovalListener { _, value, _ ->
            botProfileCache.invalidate((value as ProfileResult).profile.id)
        }
        .asCache<String, ProfileResult>()

    private val nameProfileCache = Caffeine.newBuilder()
        .expireAfterWrite(10.minutes)
        .withRemovalListener { _, value, _ ->
            botProfileCache.invalidate((value as ProfileResult).profile.id)
        }
        .asCache<String, ProfileResult>()

    private val uuidProfileCache = Caffeine.newBuilder()
        .expireAfterWrite(10.minutes)
        .withRemovalListener { key, _, _ ->
            botProfileCache.invalidate(key as UUID)
        }
        .asCache<UUID, ProfileResult>()

    private val botProfileCache = Caffeine.newBuilder()
        .asCache<UUID, ProfileResult>()

    suspend fun fetchProfile(uuid: UUID, unsigned: Boolean): ProfileResult? {
        val cachedResult = uuidProfileCache.getIfPresent(uuid)
        if (cachedResult != null) {
            return cachedResult
        }

        return MojangApi.fetchProfile(uuid, unsigned) ?: run {
            botProfileCache.get(uuid) {
                ProfileResult(GameProfile(uuid, "Bot"), emptySet())
            }
        }
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

        val mojangResult = MojangApi.hasJoined(username, serverId, address) ?: run {
            val result = botProfileCache.asMap().values.find { it.profile.name == username } ?: run {
                ProfileResult(GameProfile(UUID.randomUUID(), username)).also { botProfileCache.put(it.profile.id, it) }
            }
            HasJoinedMinecraftServerResponseImpl(result.profile.id, result.profile.properties, result.actions)
        }


        val profile = GameProfile(mojangResult.id, username).apply {
            properties.putAll(mojangResult.properties)
        }
        val result = ProfileResult(profile, mojangResult.profileActions)

        nameProfileCache.put(username, result)
        uuidProfileCache.put(mojangResult.id, result)

        return result
    }
}