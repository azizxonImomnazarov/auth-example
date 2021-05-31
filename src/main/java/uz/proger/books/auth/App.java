package uz.proger.books.auth;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CSRFHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

public class App {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    OAuth2Auth oAuth2Auth = KeycloakAuth.create(vertx, OAuth2FlowType.AUTH_CODE,
      new JsonObject("{\n" +
        "  \"realm\": \"master\",\n" +
        "  \"auth-server-url\": \"http://localhost:8080/auth/\",\n" +
        "  \"ssl-required\": \"external\",\n" +
        "  \"resource\": \"vertx\",\n" +
        "  \"credentials\": {\n" +
        "    \"secret\": \"def4c87b-d0fc-48d2-b35b-c77ad11cbd1f\"\n" +
        "  },\n" +
        "  \"confidential-port\": 0\n" +
        "}"));
    Router router = Router.router(vertx);

    OAuth2AuthHandler oAuth2AuthHandler = OAuth2AuthHandler.create(vertx,oAuth2Auth,
      "http://localhost:8090");
    oAuth2AuthHandler.setupCallback(router.get("/callback"));

    router.route("/protected/*").handler(oAuth2AuthHandler);

    router.route("/protected/somepage").handler(rc -> {
      rc.response().end("There are some secure words");
    });

    vertx.createHttpServer().requestHandler(router).listen(8090);

  }
}
