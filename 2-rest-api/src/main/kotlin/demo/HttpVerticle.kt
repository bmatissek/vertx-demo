package demo

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json

class HttpVerticle : AbstractVerticle() {
    val users = mutableMapOf<Int, User>()

    init {
        users.put(1, User("foo", 25))
        users.put(2, User("bar", 26))
    }

    override fun start(startFuture: Future<Void>?) {
        val router = Router.router(vertx)

        router.route().handler(StaticHandler.create().setWebRoot("client"))

        router.get("/users").handler({handleListUsers(it)})

        val server = vertx.createHttpServer()
                .requestHandler({router.accept(it)})
                .listen(8080)

    }

    fun handleListUsers(rc : RoutingContext) {
        val usersArr = json {
            array()
        }

        for ((id, user) in this.users) {
            usersArr.add("abcd")
        }

        rc.response().putHeader("type", "application/json")
                .end(usersArr.toString())
    }
}
