package demo

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.core.DeploymentOptions
import io.vertx.kotlin.core.VertxOptions

fun main(args: Array<String>) {
    val vertx = Vertx.vertx(VertxOptions(
        eventLoopPoolSize  = 1,
        workerPoolSize = 1
    ))

    vertx.deployVerticle(SimpleBlockingVerticle::class.java, DeploymentOptions(
            worker = true,
            instances = 2
    ))

//    vertx.deployVerticle(object : AbstractVerticle() {
//        var counter = 0
//
//        override fun start() {
//
//            vertx.setPeriodic(1000, {
//                val message = """
//                    Executing event loop verticle? ${context.isEventLoopContext}
//                    Thread: ${Thread.currentThread().name}
//                    Message: $counter
//
//                    """.trimIndent()
//
//                println (message)
//
//                counter  += 1
//            })
//        }
//    })
}

class SimpleBlockingVerticle : AbstractVerticle() {
    override fun start(startFuture: Future<Void>) {
        var counter = 0

        val timerID = vertx.setPeriodic(2000, {

            val message = """
                    Executing event loop verticle? ${context.isEventLoopContext}
                    Thread: ${Thread.currentThread().name}
                    Message: $counter

                    """.trimIndent()

            println (message)

            counter += 1

            Thread.sleep(1000)
        })

        scheduleUndeploy(9000)
        unregisterPeriodic(timerID, 12000)
    }

    fun scheduleUndeploy(milliseconds : Long) {
        vertx.setTimer(milliseconds, {
            vertx.undeploy(deploymentID())
        })
    }

    fun unregisterPeriodic(timerID : Long, milliseconds: Long) {
        vertx.setTimer(milliseconds, {
            val result = vertx.cancelTimer(timerID)

            println("SCHEDULER: $result")
        })
    }

}