package eu.describeit.cristalise.kernel.item;

import eu.describeit.cristalise.kernel.dagger.DaggerKernelComponent;
import eu.describeit.cristalise.kernel.dagger.KernelComponent;
import groovy.transform.CompileStatic;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.vertx.core.ThreadingModel.VIRTUAL_THREAD;

@Slf4j
@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
class ItemVerticleTest {

  private Item itemService;

  @BeforeAll
  @DisplayName("Deploy ItemVerticle and create service proxy")
  public void deployVerticleAndCreateProxy(Vertx vertx, VertxTestContext testContext) {
    DeploymentOptions options = new DeploymentOptions().setThreadingModel(VIRTUAL_THREAD);

    KernelComponent component = DaggerKernelComponent.create();

    vertx.deployVerticle(component.itemVerticle(), options)
      .onComplete(testContext.succeeding(id -> {
        log.info("ItemVerticle deployed with id:{}", id);
        itemService = Item.createProxy(vertx);
        testContext.completeNow();
      }));
  }

  @Test
  @DisplayName("Test requestAction method successfully")
  public void testRequestActionSuccess(VertxTestContext testContext) {
    String itemUuid = UUID.randomUUID().toString();
    String actorUuid = UUID.randomUUID().toString();
    String actionPath = "/some/action";
    String transitionID = "Start";
    String outcome = "{}";
    String fileName = null;
    List<Byte> attachment = Collections.emptyList(); // Using an empty list for simplicity

    Future<String> future = itemService.requestAction(
      itemUuid,
      actorUuid,
      actionPath,
      transitionID,
      outcome,
      fileName,
      attachment
    );

    future.onComplete(testContext.succeeding(result -> testContext.verify(() -> {
      String expectedResult = String.format("Action '%s' requested for Item %s by Actor %s", actionPath, itemUuid, actorUuid);
      Assertions.assertEquals(expectedResult, result, "The result string should match the expected format.");
      testContext.completeNow();
    })));
  }

  // Optional: Add a test case for failure scenarios if needed
  // For example, if you modify requestAction to potentially fail under certain conditions.
}
