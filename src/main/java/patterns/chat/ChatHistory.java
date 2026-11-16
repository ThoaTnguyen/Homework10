package patterns.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Messages one user has sent or received. Iterable by the other user in a message.
 */
@SuppressWarnings("rawtypes")
public class ChatHistory implements IterableByUser {
    private final User owner;
    private final List<Message> messages = new ArrayList<>();

    public ChatHistory(User owner) {
        if (owner == null) {
            throw new IllegalArgumentException("owner is required");
        }
        this.owner = owner;
    }

    public void addMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message is required");
        }
        messages.add(message);
    }

    public Message getLastMessageSent() {
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            if (message.getSender().equals(owner)) {
                return message;
            }
        }
        return null;
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public List<Message> messagesWith(User user) {
        List<Message> matches = new ArrayList<>();
        Iterator iterator = iterator(user);
        while (iterator.hasNext()) {
            matches.add((Message) iterator.next());
        }
        return List.copyOf(matches);
    }

    public Message removeMatching(MessageMemento memento, User sender) {
        if (memento == null || sender == null) {
            throw new IllegalArgumentException("memento and sender are required");
        }
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            if (message.getSender().equals(sender) && message.matches(memento)) {
                return messages.remove(i);
            }
        }
        return null;
    }

    @Override
    public Iterator iterator(User userToSearchWith) {
        return new searchMessagesByUser(messages, userToSearchWith);
    }
}
