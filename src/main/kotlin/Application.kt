package surik.simyan.locdots

import io.ktor.server.application.*
import surik.simyan.locdots.server.plugins.configureSerialization
import surik.simyan.locdots.server.plugins.configureStatusPages

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureStatusPages()
    configureDatabases()
    configureRouting()
}
