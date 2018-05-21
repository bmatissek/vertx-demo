package demo

import io.netty.util.internal.SocketUtils.accept
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

class HttpVerticle : AbstractVerticle() {
    override fun start() {
        val router = Router.router(vertx)

        router.route().handler({request->
            request.response().end("Hello vert.x from Kotlin")
        })

        vertx.createHttpServer().requestHandler({router.accept(it)}).listen(8080)
    }
}

class MainVerticle : AbstractVerticle() {
    override fun start() {
        vertx.deployVerticle("js:MyJavascriptVerticle.js")
        vertx.deployVerticle(HttpVerticle())
    }
}