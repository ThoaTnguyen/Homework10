package patterns.chat;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator over messages sent or received by one user.
 * The history is treated as fixed once iteration starts.
 */
public class searchMessagesByUser implements Iterator<Message> {
    private final List<Message> messages;
    private final User user;
    private int cursor;

    public searchMessagesByUser(List<Message> history, User userToSearchWith) {
        if (history == null) {
            throw new IllegalArgumentException("chat history is required");
        }
        if (userToSearchWith == null) {
            throw new IllegalArgumentException("user is required");
        }
        this.messages = List.copyOf(history);
        this.user = userToSearchWith;
    }

    @Override
    public boolean hasNext() {
        return nextMatch() >= 0;
    }

    @Override
    public Message next() {
        int index = nextMatch();
        if (index < 0) {
            throw new NoSuchElementException("no more messages for " + user.getName());
        }
        cursor = index + 1;
        return messages.get(index);
    }

    private int nextMatch() {
        int index = cursor;
        while (index < messages.size()) {
            if (messages.get(index).involves(user)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
