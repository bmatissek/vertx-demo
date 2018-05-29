package vertxcoroutines

import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch

const val MOVIE_PROVIDER_ADDRESS = "movie.provider"
const val GET_ALL_MOVIES = "1"
const val GET_MOVIE_BY_ID = "2"
const val CREATE_MOVIE = "3"

class MovieProviderVerticle : CoroutineVerticle(){

    private val eventBus by lazy {
        vertx.eventBus()
    }

    private fun fail (msg : String) : Nothing{
        throw IllegalStateException(msg)
    }

    private suspend fun messageHandler(msg : Message<JsonObject>) {
        when (msg.headers()["action"]) {
            GET_ALL_MOVIES -> {
                val movieList = MovieService.getAllMovies()
                val jsonArrayMovieList = movieList.map { JsonObject.mapFrom(it) }
                val res = JsonObject().put("movieList",jsonArrayMovieList)
                msg.reply(res)
            }

            GET_MOVIE_BY_ID -> {
                val id = msg.body().getInteger("id") ?: fail("Invalid 'id' parameter in Message")
                val movie = MovieService.getMovieById(id)
                movie?.let{
                    val res = JsonObject.mapFrom(it)
                    msg.reply(res)
                } ?: msg.reply( null)
            }

            CREATE_MOVIE -> {
                val name = msg.body().getString("name") ?: fail("Invalid 'name' parameter in Message")
                MovieService.createMovie(name)
                msg.reply(JsonObject().put("success",true))
            }

            else -> {
                fail ("Invalid 'action' field in option header")
            }
        }
    }

    override suspend fun start () {
        val consumer = eventBus.consumer<JsonObject>(MOVIE_PROVIDER_ADDRESS)
        consumer.handler {
            launch (vertx.dispatcher()) {
                messageHandler(it)
            }
        }
    }
}
