package demo

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx

internal val MESSAGE_ADDRESS = "vertx.ping-pong"

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(RequestResponseVerticle())
}

class RequestResponseVerticle : AbstractVerticle() {
    override fun start() {
        vertx.deployVerticle(PingVerticle())
        vertx.deployVerticle(PongVerticle())
    }
}

class PingVerticle : AbstractVerticle() {
    override fun start() {
        vertx.eventBus().send<Any>(MESSAGE_ADDRESS, "PING", {
            println(it.result().body())
            it.result().reply<Any>("ING"){
                println(it.result().body())
            }
        })
    }
}

class PongVerticle : AbstractVerticle() {
    override fun start() {
        vertx.eventBus().consumer<Any>(MESSAGE_ADDRESS, {
            println(it.body())
            it.reply<Any>("PONG") {
                println(it.result().body())
                it.result().reply("ONG")
            }
        })
    }
}
