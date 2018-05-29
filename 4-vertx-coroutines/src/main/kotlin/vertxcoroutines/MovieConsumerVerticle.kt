package vertxcoroutines

import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.eventbus.DeliveryOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch
import java.util.*

class MovieConsumerVerticle : CoroutineVerticle() {

    private val eventBus by lazy {
        vertx.eventBus()
    }

    private suspend fun getAllMovies () {
        val message = JsonObject()
        val options = DeliveryOptions().addHeader("action", GET_ALL_MOVIES)
        val res = awaitResult<Message<JsonObject>> { eventBus.send(MOVIE_PROVIDER_ADDRESS, message, options,it) }
        println (res.body())
    }

    private suspend fun getMovieById (id : Int) {
        val message = JsonObject().put("id",id)
        val options = DeliveryOptions().addHeader("action", GET_MOVIE_BY_ID)
        val res = awaitResult<Message<JsonObject>> { eventBus.send(MOVIE_PROVIDER_ADDRESS, message, options,it) }
        println (res.body())
    }

    private suspend fun createMovie (name : String) {
        val message = JsonObject().put("name",name)
        val options = DeliveryOptions().addHeader("action", CREATE_MOVIE)
        val res = awaitResult<Message<JsonObject>> {  eventBus.send(MOVIE_PROVIDER_ADDRESS, message, options,it) }
        println (res.body())
    }

    override suspend fun start() {
        createMovie("The Lord of the Rings: The Fellowship of the Ring (2001)")
        createMovie("Forrest Gump (1994)")
        getAllMovies()
        vertx.setPeriodic(3000) {
            launch(vertx.dispatcher()) {
                val randomId = Random().nextInt(14)
                println("Requesting movie with id $randomId")
                getMovieById(randomId)
            }
        }
    }
}


