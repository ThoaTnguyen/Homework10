package patterns.chat;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Chat colleague and caretaker. Sends only through {@link ChatServer}.
 * Each successful send stores a {@link MessageMemento} so the last send can be undone.
 */
@SuppressWarnings("rawtypes")
public class User implements IterableByUser {
    private final String name;
    private final ChatServer server;
    private final ChatHistory history;
    private final Deque<MessageMemento> sentMementos = new ArrayDeque<>();

    public User(String name, ChatServer server) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("user name is required");
        }
        if (server == null) {
            throw new IllegalArgumentException("chat server is required");
        }
        this.name = name;
        this.server = server;
        this.history = new ChatHistory(this);
        this.server.register(this);
    }

    public String getName() {
        return name;
    }

    public ChatHistory getChatHistory() {
        return history;
    }

    public List<Message> getMessagesWith(User user) {
        return history.messagesWith(user);
    }

    public void send(String content, User... recipients) {
        if (recipients == null || recipients.length == 0) {
            throw new IllegalArgumentException("at least one recipient is required");
        }
        Message message = new Message(this, Arrays.asList(recipients), content);
        server.sendMessage(message);
        sentMementos.addLast(message.createMemento());
        history.addMessage(message);
    }

    public void receive(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message is required");
        }
        history.addMessage(message);
    }

    public Message undoLastMessage() {
        if (sentMementos.isEmpty()) {
            throw new IllegalStateException("no sent message to undo");
        }
        MessageMemento memento = sentMementos.removeLast();
        Message removed = history.removeMatching(memento, this);
        if (removed == null) {
            sentMementos.addLast(memento);
            throw new IllegalStateException("no sent message matches the saved memento");
        }
        server.recall(removed);
        return removed;
    }

    public void block(User user) {
        server.block(this, user);
    }

    @Override
    public Iterator iterator(User userToSearchWith) {
        return history.iterator(userToSearchWith);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User other)) {
            return false;
        }
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
