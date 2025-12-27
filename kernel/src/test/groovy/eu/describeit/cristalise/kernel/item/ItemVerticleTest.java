package eu.describeit.cristalise.kernel.item;

import eu.describeit.cristalise.kernel.dagger.DaggerTestKernelComponent;
import eu.describeit.cristalise.kernel.dagger.KernelComponent;
import eu.describeit.cristalise.kernel.dagger.TestKernelComponent;
import groovy.transform.CompileStatic;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.vertx.core.ThreadingModel.WORKER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
class ItemVerticleTest {

  private final String dbSchemaChangelogFiles = "/liquibase/changelog/changelog-master.yaml";
  private final String testDataChangelogFiles = "/liquibase/changelog/changelog-testData-master.yaml";

  @BeforeAll
  @DisplayName("Deploy ItemVerticle and create service proxy")
  public void deployVerticleAndCreateProxy(Vertx vertx, VertxTestContext testContext) {
    System.setProperty("vertx-config-path", "src/test/conf/config.json");

    TestKernelComponent component = DaggerTestKernelComponent.create();
    DeploymentOptions options = component.deploymentOptions();

    PostgreSQLContainer pgContainer = component.pgContainer();
    pgContainer.start();

    component.liquibaseCommand()
      .executeUpdate(pgContainer.getJdbcUrl(), pgContainer.getUsername(), pgContainer.getPassword(), dbSchemaChangelogFiles)
      .executeUpdate(pgContainer.getJdbcUrl(), pgContainer.getUsername(), pgContainer.getPassword(), testDataChangelogFiles);

    vertx.deployVerticle(component.itemVerticle(), options)
      .onComplete(
        testContext.succeeding(id -> testContext.completeNow())
      );
  }

  @Test
  @DisplayName("requestAction fails with Item does not exists")
  public void requestAction_ItemDoesNotExists(Vertx vertx, VertxTestContext testContext) {
    ItemProxy item = new ItemProxy(vertx, UUID.randomUUID());
    String expectedMsg = String.format("Item %s does not exists", item.getItemId());

    Future<JsonObject> future = item.requestAction(UUID.randomUUID(), "/workflow/Jump", "Start", new JsonObject());

    future.onComplete(
      testContext.failing(cause -> {
        log.info("Action request failed as expected", cause);
//        assertEquals(IllegalStateException.class, cause.getClass());
        assertEquals(expectedMsg, cause.getMessage());
        testContext.completeNow();
      })
    );
  }

  /*
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

    String expectedResult = String.format("Action '%s' requested for Item %s by Actor %s", actionPath, itemUuid, actorUuid);

    Future<String> future = itemService.requestAction(itemUuid, actorUuid, actionPath, transitionID, outcome, fileName, attachment);

    future.onComplete(
      testContext
        .succeeding(result ->
          testContext.verify(() -> {
            Assertions.assertEquals(expectedResult, result);
            testContext.completeNow();
          })
        )
    );
  }
  */
}
