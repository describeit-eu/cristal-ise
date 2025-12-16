package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.EventDO
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.junit.jupiter.Testcontainers

import java.time.LocalDateTime

import static eu.describeit.cristalise.kernel.persistency.DatabaseTestUtils.await
import static org.junit.jupiter.api.Assertions.*

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
class EventRepositoryIT extends AbstractRepositoryIT {

  private EventRepository repository

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    repository = new EventRepositoryImpl(pool)
  }

  @Test
  void testFindAll() {
    def events = await(repository.findAll())
    // From 07-event.csv we inserted at least 10 rows
    assertTrue(events.size() >= 10, "There should be at least 10 events loaded from CSV")

    // spot check a known field mapping for first event
    def e0 = events.get(0)
    assertNotNull(e0.getItemId())
    assertNotNull(e0.getUserLogin())
    assertNotNull(e0.getTimestamp())
  }

  @Test
  void testInsertFindUpdateDelete() {
    // Create a new EventDO pointing to an existing item id from 02-item.csv
    def itemId = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd")
    def now = LocalDateTime.now().withNano(0)

    def toInsert = new EventDO(
      null,        // actionDesc
      null,      // actionVersion
      null,            // script
      null,       // scriptVersion
      null,   // stateMachineDesc
      null, // stateMachineVersion
      "it-user",    // userLogin
      now,                   // timestamp
      null,     // actionProperties
      itemId,                 // itemId
      "v1",                // itemVersion
      "/CityWf/UpdateCity", // actionPath
      "Done"             // transitionName
    )

    def inserted = await(repository.insert(toInsert))
    assertNotNull(inserted.getId())
    assertEquals("it-user", inserted.getUserLogin())
    assertEquals(itemId, inserted.getItemId())
    assertEquals("v1", inserted.getItemVersion())
    assertEquals("/CityWf/UpdateCity", inserted.getActionPath())

    def fetchedOpt = await(repository.findById(inserted.getId()))
    assertTrue(fetchedOpt.isPresent())
    assertEquals(inserted, fetchedOpt.get())

    // Update a couple of fields
    inserted.setUserLogin("it-user-upd")
    inserted.setActionVersion("v2")
    def updatedOpt = await(repository.update(inserted))
    assertTrue(updatedOpt.isPresent())
    def updated = updatedOpt.get()
    assertEquals(inserted.getId(), updated.getId())
    assertEquals("it-user-upd", updated.getUserLogin())
    assertEquals("v2", updated.getActionVersion())

    // Delete
    def rows = await(repository.deleteById(updated.getId()))
    assertEquals(1, rows)
    def afterDelete = await(repository.findById(updated.getId()))
    assertTrue(afterDelete.isEmpty())
  }

  @Test
  void testFindByItemId() {
    def budapest = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd")
    def events = await(repository.findByItemId(budapest))
    assertTrue(events.size() >= 1, "Expected at least one event for Budapest item")
    assertTrue(events.stream().allMatch(e -> budapest.equals(e.getItemId())))
  }

  @Test
  void testDeleteByItemId() {
    // Use an item that initially has no events in CSV (Delhi)
    def delhi = UUID.fromString("bbcb31f8-7f4c-47fb-8876-864a61e48d5d")

    def base = LocalDateTime.now().withNano(0)
    def ev1 = new EventDO(null, null, null, null, null, null, "u1", base, null, delhi, "v1", "/CityWf", "Start")
    def ev2 = new EventDO(null, null, null, null, null, null, "u2", base.plusMinutes(1), null, delhi, "v1", "/CityWf/UpdateCity", "Done")

    await(repository.insert(ev1))
    await(repository.insert(ev2))

    def before = await(repository.findByItemId(delhi))
    assertEquals(2, before.size())

    def deleted = await(repository.deleteByItemId(delhi))
    assertEquals(2, deleted)

    def after = await(repository.findByItemId(delhi))
    assertTrue(after.isEmpty())
  }
}
