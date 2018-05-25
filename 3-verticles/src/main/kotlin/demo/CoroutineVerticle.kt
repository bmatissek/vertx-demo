package demo

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.future.asCompletableFuture
import java.util.concurrent.CompletableFuture

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(CoroutineExample())

}

class CoroutineExample : CoroutineVerticle() {

    suspend fun retorna1Apos1Segundo() : Int{
        delay(1000)
        return 1
    }

    suspend fun retorna2Apos1Segundo() : Int{
        delay(1000)
        return 2
    }

    suspend fun retorna3Apos1Segundo() : Int{
        delay(1000)
        return 3
    }

    override suspend fun start (){
        val a : CompletableFuture<Int> = async { retorna1Apos1Segundo() }.asCompletableFuture()
        val b : CompletableFuture<Int> = async { retorna2Apos1Segundo() }.asCompletableFuture()
        val c : CompletableFuture<Int> = async{ retorna3Apos1Segundo() }.asCompletableFuture()
    }



//    fun disparaACada1Segundo(func: suspend () -> Any?): Job {
//        return launch(vertx.dispatcher()) {
//            while (true) {
//                func()
//                delay(1000)
//            }
//        }
//    }
//
//
//    suspend fun demora2Segundos() {
//        println("Executando func")
//        delay(2000)
//        println("Esperei 2 segundos")
//    }
//
//    override suspend fun start() {
//        val listJob = List(10) {
//            disparaACada1Segundo {
//                demora2Segundos()
//            }
//        }
//        for (job in listJob)
//            println(job.isActive)
//
//    }

}
