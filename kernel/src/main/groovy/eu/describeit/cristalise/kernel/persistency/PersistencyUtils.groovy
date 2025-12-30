package eu.describeit.cristalise.kernel.persistency

import groovy.transform.CompileStatic
import io.vertx.core.Future

import java.util.stream.Collectors
import java.util.stream.StreamSupport

/**
 * Small helper utilities to reduce duplicated code across repository implementations.
 */
@CompileStatic
final class PersistencyUtils {

  private PersistencyUtils() {}

  /**
   * Returns the first element of the iterable wrapped in an Optional, or Optional.empty() if none.
   */
  static <T> Optional<T> firstOptional(Iterable<T> rs) {
    Iterator<T> it = rs.iterator()
    return it.hasNext() ? Optional.ofNullable(it.next()) : Optional.empty() as Optional<T>
  }

  /**
   * Converts an Iterable (e.g., RowSet) to a List preserving iteration order.
   */
  static <T> List<T> toList(Iterable<T> rs) {
    return StreamSupport.stream(rs.spliterator(), false).collect(Collectors.toList())
  }

  /**
   * Returns a Future of the first element from the iterable or a failed Future with the given message.
   * Useful for INSERT ... RETURNING queries.
   */
  static <T> Future<T> firstOrFail(Iterable<T> rs, String failMessage) {
    Iterator<T> it = rs.iterator()
    if (it.hasNext()) return Future.succeededFuture(it.next())
    else              return Future.failedFuture(failMessage)
  }
}
