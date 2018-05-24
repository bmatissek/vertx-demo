package demo

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.DeploymentOptions
import io.vertx.kotlin.core.VertxOptions
import io.vertx.kotlin.core.eventbus.DeliveryOptions
import io.vertx.kotlin.core.http.HttpServerOptions

internal val TIMEOUT_MESSAGE_ADDRESS = "timeout.message"

fun main(args : Array<String>){
    val vertx = Vertx.vertx(VertxOptions(
            eventLoopPoolSize = 40
    ))

    vertx.deployVerticle(TimeoutSenderVerticle::class.java, DeploymentOptions(instances = 2))
    vertx.deployVerticle(TimeoutReceiverVerticle::class.java, DeploymentOptions(instances = 32))
}

class TimeoutSenderVerticle : AbstractVerticle() {
    override fun start() {
        vertx.setPeriodic(1000, {
            println("Send from ${Thread.currentThread().id}")
            vertx.eventBus().send(TIMEOUT_MESSAGE_ADDRESS, "message", DeliveryOptions(sendTimeout = 1000))
        })
    }
}

class TimeoutReceiverVerticle : AbstractVerticle() {
    override fun start() {
        val router = Router.router(vertx)

        router.route().handler{
            it.response().
                    putHeader("Content-type","text/plain").
                    setStatusCode(200).
                    end("OK: ${Thread.currentThread().id}\n")
        }

        vertx.createHttpServer(HttpServerOptions(
                port = 8080
        )).requestHandler({router.accept(it)}).listen()


        vertx.eventBus().consumer<Any>(TIMEOUT_MESSAGE_ADDRESS) {
            println("New message: thread - ${Thread.currentThread().id} - eventloop: ${it.body()}")
        }
    }
}