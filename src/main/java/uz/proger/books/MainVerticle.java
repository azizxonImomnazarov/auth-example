package uz.proger.books;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private final BooksStore booksStore = new BooksStore();
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(MainVerticle.class,new DeploymentOptions().setInstances(4));
    vertx.deployVerticle(PublishVerticle.class,new DeploymentOptions().setInstances(4));
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.debug("starting ....");
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    //GET -> FETCH ONE BOOK
    getBookByISBN(booksStore, router);
    // GET -> FETCH ALL BOOKS
    getAllBooks(booksStore, router);
    // POST -> CREATE ONE BOOK
    saveBook(booksStore, router);
    // PUT   -> UPDATE ONE BOOK
    updateBook(booksStore, router);
    // DELETE -> DELETE ONE BOOK
    deleteBook(booksStore, router);

    errorHandler(router);

    vertx.eventBus().consumer("ping-pong", handler -> {
      LOGGER.info("{}",handler.body());
    });

    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOGGER.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private void errorHandler(Router router) {
    router.errorHandler(500, error -> {
      LOGGER.error("Failed : " , error.failure());
      error.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(new JsonObject().put("error", error.failure().getMessage()).encode());
    });
  }

  private void deleteBook(BooksStore booksStore, Router router) {
    router.delete("/api/books/:isbn")
      .handler(rc -> {
        final String isbn = rc.pathParam("isbn");
        final JsonObject book = rc.getBodyAsJson();
        final Book deletedBook = booksStore.delete(isbn, book.mapTo(Book.class));
        if (deletedBook == null) {
          rc.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject().put("error","Book not found").encode());
        } else {
          rc.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(JsonObject.mapFrom(deletedBook).encode());
        }
      });
  }

  private void updateBook(BooksStore booksStore, Router router) {
    router.put("/api/books/:isbn")
      .handler(rc -> {
        final String isbn = rc.pathParam("isbn");
        final JsonObject book = rc.getBodyAsJson();
        final Book updatedBook = booksStore.update(isbn, book.mapTo(Book.class));
        rc.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(JsonObject.mapFrom(updatedBook).encode());
      });
  }

  private void saveBook(BooksStore booksStore, Router router) {
    router.post("/api/books")
      .handler(rc -> {
        final JsonObject book = rc.getBodyAsJson();
        booksStore.add(book.mapTo(Book.class));
        rc.response()
          .setStatusCode(HttpResponseStatus.CREATED.code())
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(book.encode());
      });
  }

  private void getAllBooks(BooksStore booksStore, Router router) {
    router.get("/api/books")
      .handler(rc -> {
        rc.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(booksStore.getAllBooks().encode());
      });
  }

  private void getBookByISBN(BooksStore booksStore, Router router) {
    router.get("/api/books/:isbn")
      .handler(rc -> {
        final String isbn = rc.pathParam("isbn");
        rc.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(JsonObject.mapFrom(booksStore.getBookByIsbn(isbn)).encode());
      });
  }
}
