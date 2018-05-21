import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

class MainVerticle : AbstractVerticle() {
    override fun start(startFuture: Future<Void>?) {
        vertx.createHttpServer().requestHandler({
            it.response().end("Hello World")
        }).listen(8080)
    }
}