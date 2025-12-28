package eu.describeit.cristalise.kernel.item;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

import java.util.List;

@ProxyGen // Generate the proxy and handler
@VertxGen // Generate clients in non-java languages
public interface Item {
    String ADDRESS = "cristalise-items";

    Future<String> requestAction(
            String     itemUuid,
            String     actorUuid,
            String     actionPath,
            String     transitionID,
            String     outcome,
            String     fileName,
            List<Byte> attachment
    );
}
