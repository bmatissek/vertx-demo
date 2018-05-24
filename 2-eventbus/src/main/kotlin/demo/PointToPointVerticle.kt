package demo

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.util.*

fun main(args : Array<String>) {
    val vertxOptions = VertxOptions(json{obj(
            "eventLoopPoolSize" to 6
    )})

    var vertx = Vertx.vertx(vertxOptions)
    vertx.deployVerticle(PointToPointVerticle())

}

class PointToPointVerticle : AbstractVerticle() {

    override fun start() {
        println("Deployed Point to Point Verticle")

        vertx.deployVerticle(SenderVerticle())
        vertx.deployVerticle(ReceiverVerticle(1))
        vertx.deployVerticle(ReceiverVerticle(2, 8000))
        vertx.deployVerticle(ReceiverVerticle(3, 4000))

        vertx.setTimer(15000, {
            vertx.deployVerticle(ReceiverVerticle(4, 12000))
        })

    }

    override fun stop() {
        println("Undeployed Point to Point Verticle")
    }
}

class ReceiverVerticle(val id: Int, val timeout : Long = 0) : AbstractVerticle() {
    override fun start() {
        println("Deployed ReceiverVerticle ${id}")

        val consumer = vertx.eventBus().consumer<JsonObject>("message.receiver")

        consumer.handler{
            val message = it.body().getString("message")
            val messageID = it.body().getString("id")

            println("Receiver $id - MessageID: $messageID - Message: $message")

        }

        scheduleUnregister(consumer)
    }

    fun scheduleUnregister(consumer : MessageConsumer<JsonObject>) {
        if (timeout > 0){

            vertx.setTimer(timeout, {
                consumer.unregister(){
                    if (it.succeeded()) {
                        println("Unregistered ReceiverVerticle ${id}")
                    } else {
                        throw it.cause()
                    }
                }
            })

        }
    }

    override fun stop() {
        println("Undeployed ReceiverVerticle ${id}")
    }
}

class SenderVerticle() : AbstractVerticle() {

    var counter = 0
    val random = Random()

    override fun start() {
        println("Deployed SenderVerticle")

        vertx.setPeriodic(1000, {
            sendMessage()
        })
    }

    fun sendMessage() {
        val randomMessage = getRandomMessage()
        vertx.eventBus().send("message.receiver", json {obj(
                "message" to randomMessage,
                "id" to counter.toString()
        )})
        counter += 1
    }

    fun getRandomMessage() : String {
        val messagePool = listOf(
                "O Brasil jogará contra a Suíça na Copa do Mundo",
                "Mark Zuckerberg goes on trial tomorrow",
                "中国新年明天开始",
                "Un nouveau restaurant français ouvre ses portes en ville",
                "Tranquila Tókio, Tranquila"
        )

        val index = random.nextInt(messagePool.size)
        return messagePool[index]
    }

    override fun stop() {
        println("Undeployed SenderVerticle")
    }
}