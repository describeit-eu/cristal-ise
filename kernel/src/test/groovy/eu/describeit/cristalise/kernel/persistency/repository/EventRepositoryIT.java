package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.EventDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static eu.describeit.cristalise.kernel.persistency.utils.DatabaseTestUtils.await;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventRepositoryIT extends AbstractRepositoryIT {

  private EventRepository repository;

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll();
    repository = new EventRepositoryImpl(pool);
  }

  @Test
  void testFindAll() {
    List<EventDO> events = await(repository.findAll());
    // From 07-event.csv we inserted at least 10 rows
    assertTrue(events.size() >= 10, "There should be at least 10 events loaded from CSV");

    // spot check a known field mapping for first event
    EventDO e0 = events.get(0);
    assertNotNull(e0.getItemId());
    assertNotNull(e0.getUserLogin());
    assertNotNull(e0.getTimestamp());
  }

  @Test
  void testInsertFindUpdateDelete() {
    // Create a new EventDO pointing to an existing item id from 02-item.csv
    UUID itemId = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd");
    LocalDateTime now = LocalDateTime.now().withNano(0);

    EventDO toInsert = new EventDO(
      null,              // actionDesc
      null,              // actionVersion
      null,              // script
      null,              // scriptVersion
      null,              // stateMachineDesc
      null,              // stateMachineVersion
      "it-user",         // userLogin
      now,               // timestamp
      null,              // actionProperties
      itemId,            // itemId
      "v1",              // itemVersion
      "/CityWf/UpdateCity", // actionPath
      "Done"             // transitionName
    );

    EventDO inserted = await(repository.insert(toInsert));
    assertNotNull(inserted.getId());
    assertEquals("it-user", inserted.getUserLogin());
    assertEquals(itemId, inserted.getItemId());
    assertEquals("v1", inserted.getItemVersion());
    assertEquals("/CityWf/UpdateCity", inserted.getActionPath());

    Optional<EventDO> fetchedOpt = await(repository.findById(inserted.getId()));
    assertTrue(fetchedOpt.isPresent());
    assertEquals(inserted, fetchedOpt.get());

    // Update a couple of fields
    inserted.setUserLogin("it-user-upd");
    inserted.setActionVersion("v2");
    Optional<EventDO> updatedOpt = await(repository.update(inserted));
    assertTrue(updatedOpt.isPresent());
    EventDO updated = updatedOpt.get();
    assertEquals(inserted.getId(), updated.getId());
    assertEquals("it-user-upd", updated.getUserLogin());
    assertEquals("v2", updated.getActionVersion());

    // Delete
    int rows = await(repository.deleteById(updated.getId()));
    assertEquals(1, rows);
    Optional<EventDO> afterDelete = await(repository.findById(updated.getId()));
    assertTrue(afterDelete.isEmpty());
  }

  @Test
  void testFindByItemId() {
    UUID budapest = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd");
    List<EventDO> events = await(repository.findByItemId(budapest));
    assertTrue(events.size() >= 1, "Expected at least one event for Budapest item");
    assertTrue(events.stream().allMatch(e -> budapest.equals(e.getItemId())));
  }

  @Test
  void testDeleteByItemId() {
    // Use an item that initially has no events in CSV (Delhi)
    UUID delhi = UUID.fromString("bbcb31f8-7f4c-47fb-8876-864a61e48d5d");

    LocalDateTime base = LocalDateTime.now().withNano(0);
    EventDO ev1 = new EventDO(null, null, null, null, null, null, "u1", base, null, delhi, "v1", "/CityWf", "Start");
    EventDO ev2 = new EventDO(null, null, null, null, null, null, "u2", base.plusMinutes(1), null, delhi, "v1", "/CityWf/UpdateCity", "Done");

    await(repository.insert(ev1));
    await(repository.insert(ev2));

    List<EventDO> before = await(repository.findByItemId(delhi));
    assertEquals(2, before.size());

    int deleted = await(repository.deleteByItemId(delhi));
    assertEquals(2, deleted);

    List<EventDO> after = await(repository.findByItemId(delhi));
    assertTrue(after.isEmpty());
  }
}
