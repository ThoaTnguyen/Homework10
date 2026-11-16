package patterns.chat;

import java.util.Iterator;

/**
 * Iteration needs the user to search for, so this is not {@link java.lang.Iterable}.
 */
@SuppressWarnings("rawtypes")
public interface IterableByUser {
    Iterator iterator(User userToSearchWith);
}
