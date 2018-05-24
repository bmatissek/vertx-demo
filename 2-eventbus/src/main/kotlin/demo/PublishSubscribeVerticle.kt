package demo

import io.vertx.core.*
import java.util.*

fun main(args : Array<String>) {
    val vertx = Vertx.vertx()

    vertx.deployVerticle(PublishSubscribeVerticle(), {res->
        if (res.succeeded()) {
            println("Deploy succeeded")
            vertx.setTimer(20000, {
                println("Undeployment timeout reached for ${res.result()}")
                vertx.undeploy(res.result())
            })
        } else {
            println(res.cause())
            vertx.close()
        }
    })

    vertx.setTimer(30000, {
        vertx.close()
    })
}

class PublishSubscribeVerticle : AbstractVerticle() {

    lateinit var consumer2DeploymentID : String

    override fun start(startFuture: Future<Void>) {
        println("Deploy PublishSubscribeVerticle")

        val publishVerticleDeployment = Future.future<String>()
        val consumer1VerticleDeployment = Future.future<String>()
        val consumer2VerticleDeployment = Future.future<String>()

        vertx.deployVerticle(PublishVerticle(), publishVerticleDeployment.completer())

        vertx.deployVerticle(SubscribeVerticle(1), consumer1VerticleDeployment.completer())
        vertx.deployVerticle(SubscribeVerticle(2), {
            if (it.succeeded()){
                consumer2DeploymentID = it.result()
                consumer2VerticleDeployment.complete()
            } else {
                consumer2VerticleDeployment.fail(it.cause())
            }
        })

        val deploymentFuture = CompositeFuture.all(
                publishVerticleDeployment,
                consumer1VerticleDeployment,
                consumer2VerticleDeployment)

        deploymentFuture.setHandler {
            if (it.succeeded()) {
                scheduleUndeploy(consumer2DeploymentID, 10000, null)
                startFuture.complete()
            }
            else
                startFuture.fail(it.cause())
        }
    }

    fun scheduleUndeploy(deploymentID : String, milliseconds: Long, handler: Handler<AsyncResult<Void>>?) {
        vertx.setTimer(milliseconds, {
            vertx.undeploy(deploymentID)
        })
    }

    override fun stop() {
        println("Undeploy PublishSubscribeVerticle")
    }
}

class PublishVerticle : AbstractVerticle() {

    var curMessageID = 0
    val random = Random()

    override fun start() {
        vertx.setPeriodic(3000, { id ->

            val randomMessage = getRandomMessage()
            val publishMessage = "Message: $curMessageID - $randomMessage"

            vertx.eventBus().publish("bizzarre.news.feed", publishMessage)

            curMessageID += 1
        })

        println("PublishVerticle Deployed")
    }

    override fun stop() {
        println("Publish verticle undeployed")
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

}

class SubscribeVerticle(val id: Int) : AbstractVerticle() {

    override fun start() {
        vertx.eventBus().consumer<Any>("bizzarre.news.feed", {
            val consumerMessage = "${it.body()}"
            println("Consumer ${this.id} - $consumerMessage")
        })

        vertx.setTimer(2000, {
            if (this.id == 2) {
//                try {
                    throw Exception("Error in SubscribeVerticle")
//                } catch(e : Exception){
//                    vertx.undeploy(this.deploymentID())
//                }
            }
        })

        println("Subscribe ${this.id} verticle deployed")
    }

    override fun stop() {
        println("Subscribe ${this.id} verticle undeployed")
    }
}
