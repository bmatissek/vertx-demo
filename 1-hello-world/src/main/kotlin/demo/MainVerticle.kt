package demo

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx

fun main(args : Array<String>) {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
}

class MainVerticle : AbstractVerticle() {
    override fun start() {
        vertx.createHttpServer()
            .requestHandler({request ->
                request.response().end("Hello World")
            })
            .listen(8080)
    }
}