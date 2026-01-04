package eu.describeit.cristalise.kernel.item


import eu.describeit.cristalise.kernel.persistency.repository.AbstractRepositoryIT
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.CommonTestItemIds.BUDAPEST
import static org.junit.jupiter.api.Assertions.*

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@CompileStatic
class ItemProxyIT extends AbstractRepositoryIT {

  @Test
  void testGetLifeCycle() {
    // Create ItemProxy for Budapest
    ItemProxy item = ItemProxy.create(pool, BUDAPEST.getUuid()).await()

    assertNotNull(item)
    assertEquals("Budapest", item.getName())

    // Get lifecycle
    def lifecycle = item.getLifeCycle().await()

    assertNotNull(lifecycle)
    assertEquals("CapitalWf", lifecycle.name)
    assertEquals("/CapitalWf", lifecycle.path)
    assertEquals(2, lifecycle.actions.size())
    assertEquals(['ChangeState','UpdateCapital'], lifecycle.actions.collect {it.name}.sort())
  }
}
