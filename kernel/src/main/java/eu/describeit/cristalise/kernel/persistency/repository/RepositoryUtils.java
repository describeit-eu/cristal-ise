package eu.describeit.cristalise.kernel.persistency.repository;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Small helper utilities to reduce duplicated code across repository implementations.
 */
public final class RepositoryUtils {

  private RepositoryUtils() {}

  /**
   * Returns the first element of the iterable wrapped in an Optional, or Optional.empty() if none.
   */
  public static <T> Optional<T> firstOptional(Iterable<T> rs) {
    Iterator<T> it = rs.iterator();
    return it.hasNext() ? Optional.ofNullable(it.next()) : Optional.empty();
  }

  /**
   * Converts an Iterable (e.g., RowSet) to a List preserving iteration order.
   */
  public static <T> List<T> toList(Iterable<T> rs) {
    List<T> list = new ArrayList<>();
    for (T row : rs) list.add(row);
    return list;
  }

  /**
   * Returns a Future of the first element from the iterable or a failed Future with the given message.
   * Useful for INSERT ... RETURNING queries.
   */
  public static <T> Future<T> firstOrFail(Iterable<T> rs, String failMessage) {
    Iterator<T> it = rs.iterator();
    if (it.hasNext()) return Future.succeededFuture(it.next());
    else              return Future.failedFuture(failMessage);
  }
}
