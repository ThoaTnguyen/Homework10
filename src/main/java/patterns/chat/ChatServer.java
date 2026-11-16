package patterns.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mediator. Users register here and never deliver messages to each other directly.
 * A block drops messages from one user to the user who blocked them.
 */
public class ChatServer {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Set<String>> blockedBy = new HashMap<>();

    public void register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user is required");
        }
        if (users.containsKey(user.getName())) {
            throw new IllegalArgumentException("user is already registered: " + user.getName());
        }
        users.put(user.getName(), user);
    }

    public void unregister(User user) {
        if (user == null || !users.containsKey(user.getName())) {
            throw new IllegalArgumentException("user is not registered");
        }
        users.remove(user.getName());
        blockedBy.remove(user.getName());
        for (Set<String> blocked : blockedBy.values()) {
            blocked.remove(user.getName());
        }
    }

    public boolean isRegistered(User user) {
        return user != null && users.containsKey(user.getName());
    }

    public void block(User blocker, User blocked) {
        requireRegistered(blocker);
        requireRegistered(blocked);
        if (blocker.equals(blocked)) {
            throw new IllegalArgumentException("a user cannot block themselves");
        }
        blockedBy.computeIfAbsent(blocker.getName(), name -> new HashSet<>()).add(blocked.getName());
    }

    public boolean isBlocking(User blocker, User blocked) {
        if (blocker == null || blocked == null) {
            return false;
        }
        Set<String> blockedNames = blockedBy.get(blocker.getName());
        return blockedNames != null && blockedNames.contains(blocked.getName());
    }

    public void sendMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message is required");
        }
        requireRegistered(message.getSender());
        for (User recipient : message.getRecipients()) {
            requireRegistered(recipient);
        }
        for (User recipient : message.getRecipients()) {
            if (!isBlocking(recipient, message.getSender())) {
                recipient.receive(message);
            }
        }
    }

    public void recall(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message is required");
        }
        MessageMemento memento = message.createMemento();
        for (User recipient : message.getRecipients()) {
            recipient.getChatHistory().removeMatching(memento, message.getSender());
        }
    }

    private void requireRegistered(User user) {
        if (!isRegistered(user)) {
            String name = user == null ? "null" : user.getName();
            throw new IllegalArgumentException("user is not registered: " + name);
        }
    }
}
