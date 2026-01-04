package eu.describeit.cristalise

import eu.describeit.cristalise.kernel.persistency.domain.*
import groovy.transform.CompileStatic
import org.apache.commons.lang3.math.NumberUtils

@CompileStatic
class TestDataIdUtils {
  private static final Map<Class<?>, String> CLASS_TO_FILE = [
    (ActionDO)          : "01-action.csv",
    (ItemDO)            : "02-item.csv",
    (DomainPathDO)      : "03-domainPath.csv",
    (CollectionDO)      : "04-collections.csv",
    (CollectionMemberDO): "05-collection-member.csv",
    (JobDO)             : "06-job.csv",
    (EventDO)           : "07-event.csv",
    (OutcomeDO)         : "08-outcome.csv",
    (AttachmentDO)      : "09-attachment.csv",
    (ItemPropertyDO)    : "10-itemProperty.csv",
    (ViewPointDO)       : "11-viewPoint.csv"
  ] as Map<Class<?>, String>

  static Object getId(Class<?> clazz, String name) {
    String fileName = CLASS_TO_FILE.get(clazz)
    if (!fileName) {
      throw new IllegalArgumentException("No test data file mapped for class ${clazz.name}")
    }

    InputStream is = TestDataIdUtils.classLoader.getResourceAsStream("data/$fileName")
    if (!is) {
      throw new IllegalStateException("Test data file data/$fileName not found in classpath")
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(is))
    String headerLine = reader.readLine()

    if (!headerLine) return null

    List<String> headers = headerLine.split(";") as List
    int nameIndex = headers.indexOf("name")
    int idIndex = headers.indexOf("id")

    if (nameIndex == -1) {
      // Fallback to 'path' if 'name' is missing, e.g. for DomainPathDO
      nameIndex = headers.indexOf("path")
      if (nameIndex == -1) {
        throw new IllegalArgumentException("CSV file $fileName does not have 'name' or 'path' column")
      }
    }

    String line
    long rowIndex = 1
    while ((line = reader.readLine()) != null) {
      if (line.trim().isEmpty()) continue
      String[] parts = line.split(";", -1)
      if (parts.length > nameIndex && parts[nameIndex] == name) {
        if (idIndex != -1 && parts.length > idIndex && parts[idIndex] && parts[idIndex] != "NULL") {
          String idStr = parts[idIndex]
          if (NumberUtils.isParsable(idStr)) return NumberUtils.createLong(idStr)
          else                               return UUID.fromString(idStr)
        } else {
          return rowIndex
        }
      }
      rowIndex++
    }
    return null
  }

  static Long getLongId(Class<?> clazz, String name) {
    Object id = getId(clazz, name)
    if (id == null) return null
    if (id instanceof Long) return (Long) id
    return Long.valueOf(id.toString())
  }

  static UUID getUUID(Class<?> clazz, String name) {
    Object id = getId(clazz, name)
    if (id == null) return null
    if (id instanceof UUID) return (UUID) id
    return UUID.fromString(id.toString())
  }

  static Long getActionId(String name) {
    getLongId(ActionDO, name)
  }

  static UUID getItemId(String name) {
    getUUID(ItemDO, name)
  }

  static Long getDomainPathId(String name) {
    getLongId(DomainPathDO, name)
  }

  static Long getCollectionId(String name) {
    getLongId(CollectionDO, name)
  }

  static Long getCollectionMemberId(String name) {
    getLongId(CollectionMemberDO, name)
  }

  static Long getJobId(String name) {
    getLongId(JobDO, name)
  }

  static Long getEventId(String name) {
    getLongId(EventDO, name)
  }

  static Long getOutcomeId(String name) {
    getLongId(OutcomeDO, name)
  }

  static Long getAttachmentId(String name) {
    getLongId(AttachmentDO, name)
  }

  static Long getItemPropertyId(String name) {
    getLongId(ItemPropertyDO, name)
  }

  static Long getViewPointId(String name) {
    getLongId(ViewPointDO, name)
  }
}
