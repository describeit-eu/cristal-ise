package eu.describeit.cristalise.kernel.item;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.List;

@ProxyGen // Generate the proxy and handler
@VertxGen // Generate clients in non-java languages
public interface Item {
    public static final String ADDRESS = "cristalise-items";

    public Future<String> requestAction(
            String     itemUuid,
            String     actorUuid,
            String     actionPath,
            String     transitionID,
            String     outcome,
            String     fileName,
            List<Byte> attachment
    );

    static Item createProxy(Vertx vertx) {
        return new ItemVertxEBProxy(vertx, ADDRESS);
    }
}
