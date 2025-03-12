package dev.slne.authserver.server.routes.session.minecraft

import dev.slne.authserver.server.routes.session.minecraft.hasJoined.hasJoined
import dev.slne.authserver.server.routes.session.minecraft.join.join
import dev.slne.authserver.server.routes.session.minecraft.profile.profile
import io.ktor.server.routing.*

fun Route.minecraft() {
    route("hasJoined") { hasJoined() }
    route("join") { join() }
    route("profile") { profile() }
}