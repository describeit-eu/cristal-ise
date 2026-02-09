package eu.describeit.cristalise.kernel.process

import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
import eu.describeit.cristalise.kernel.persistency.domain.*
import eu.describeit.cristalise.kernel.persistency.repository.AbstractRepositoryIT
import eu.describeit.cristalise.kernel.persistency.repository.ItemRepository
import eu.describeit.cristalise.kernel.persistency.repository.ItemRepositoryImpl
import groovy.transform.CompileStatic
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
class BootstrapIT extends AbstractRepositoryIT {
  private RepositoryWrapper storage
  private DomainPathDO rootPath = new DomainPathDO(path: Bootstrap.rootItemPath)

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    storage = new RepositoryWrapper(pool)
  }

  @Test
  void "should create CristaliseRoot when it doesn't exist"() {
    Bootstrap bootstrap = component.bootstrap()

    // Ensure it doesn't exist
    assertFalse storage.exists(rootPath).await()

    bootstrap.execute().await()

    UUID rootId = storage.getItemId(rootPath).await()
    ItemDO rootAfter = storage.getItemDO(rootId).await()
    assertEquals("CristaliseRoot", rootAfter.name)
    assertEquals("RootItem", rootAfter.type)

    RepositoryWrapper storage = new RepositoryWrapper(pool)

    // Check DomainPath
    DomainPathDO dp = storage.getDomainPathByPath(Bootstrap.rootItemPath).await()
    assertEquals(rootId, dp.itemId)

    // Check Properties
    List<ItemPropertyDO> props = storage.getItemPropertiesByItemId(rootId).await()
    assertTrue(props.any { it.name == 'Name' && it.value == 'CristaliseRoot' })
    assertTrue(props.any { it.name == 'Type' && it.value == 'RootItem' })

    // Check Event
    List<EventDO> events = storage.getEventsByItemId(rootId).await()
    assertEquals(1, events.size())
    assertEquals("bootstrap", events[0].actionPath)

    // when - call it again
    bootstrap.execute().await()

    // then there is only one
    List<UUID> itemIds = storage.getItemIdsOfSameType('RootItem').await()
    assertEquals(1, itemIds.size())
    assertEquals(rootAfter.id, itemIds[0])
  }
}
