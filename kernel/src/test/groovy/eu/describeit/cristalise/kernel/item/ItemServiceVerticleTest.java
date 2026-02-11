package eu.describeit.cristalise.kernel.item;

import eu.describeit.cristalise.kernel.dagger.DaggerTestKernelComponent;
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

import java.util.UUID;

import static eu.describeit.cristalise.CommonTestItemIds.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
class ItemServiceVerticleTest {

  private static final String dbSchemaChangelogFiles = "/liquibase/changelog/changelog-master.yaml";
  private static final String testDataChangelogFiles = "/liquibase/changelog/changelog-testData-master.yaml";

  @BeforeAll
  @DisplayName("Deploy ItemServiceVerticle")
  public void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    System.setProperty("vertx-config-path", "src/test/conf/config.json");

    TestKernelComponent component = DaggerTestKernelComponent.create();
    DeploymentOptions options = component.deploymentOptions();

    PostgreSQLContainer pgContainer = component.pgContainer();
    pgContainer.start();

    component.liquibaseCommand()
      .executeUpdate(pgContainer.getJdbcUrl(), pgContainer.getUsername(), pgContainer.getPassword(), dbSchemaChangelogFiles)
      .executeUpdate(pgContainer.getJdbcUrl(), pgContainer.getUsername(), pgContainer.getPassword(), testDataChangelogFiles);

    vertx.deployVerticle(component.itemServiceVerticle(), options)
      .onComplete(
        testContext.succeeding(id -> testContext.completeNow())
      );
  }

  @Test
  @DisplayName("requestAction fails with Item does not exists")
  public void requestAction_ItemDoesNotExists(Vertx vertx, VertxTestContext testContext) {
    ItemProxy item = new ItemProxy(vertx, UUID.randomUUID());
    String expectedMsg = String.format("Item %s does not exists", item.getItemId());

    String actionPath = "/workflow/Jump";
    String transitionID = "Start";

    Future<JsonObject> future = item.requestAction(UUID.randomUUID(), actionPath, transitionID, new JsonObject());

    future.onComplete(
      testContext.failing(cause -> {
        log.info("Action request failed as expected", cause);
//        assertEquals(IllegalStateException.class, cause.getClass());
        assertEquals(expectedMsg, cause.getMessage());
        testContext.completeNow();
      })
    );
  }

  @Test
  @DisplayName("requestAction completes successfully")
  public void testRequestActionSuccess(Vertx vertx, VertxTestContext testContext) {
    ItemProxy item = new ItemProxy(vertx, BUDAPEST.getUuid());
    JsonObject outcome = new JsonObject().put("request", "OK");
    JsonObject expectedOutcome = outcome.copy().put("name", "Budapest");

    String actionPath = "CapitalWf/UpdateCapital";
    String transitionID = "Start";

    Future<JsonObject> future = item.requestAction(UUID.randomUUID(), actionPath, transitionID, outcome);

    future
      .onComplete(
        testContext.succeeding(result ->
          testContext.verify(() -> {
            log.info("{}", result);
            assertEquals(expectedOutcome, result);
            testContext.completeNow();
          })
        )
      )
      .onFailure(testContext::failNow);
  }
}
