package surik.simyan.locdots

import io.ktor.server.application.*
import surik.simyan.locdots.server.plugins.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureRouting()
}
