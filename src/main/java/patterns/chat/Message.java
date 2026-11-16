package patterns.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Originator for the Memento pattern. A message stores who sent it, who it was for,
 * when it was sent, and the text. {@link #createMemento()} captures the text and time.
 */
public class Message {
    private final User sender;
    private final List<User> recipients;
    private final LocalDateTime timestamp;
    private final String content;

    public Message(User sender, List<User> recipients, String content) {
        this(sender, recipients, content, LocalDateTime.now());
    }

    Message(User sender, List<User> recipients, String content, LocalDateTime timestamp) {
        if (sender == null) {
            throw new IllegalArgumentException("sender is required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("message content is required");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp is required");
        }
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("at least one recipient is required");
        }
        List<User> copy = new ArrayList<>();
        for (User recipient : recipients) {
            if (recipient == null) {
                throw new IllegalArgumentException("recipient is required");
            }
            if (recipient.equals(sender)) {
                throw new IllegalArgumentException("a user cannot message themselves");
            }
            if (!copy.contains(recipient)) {
                copy.add(recipient);
            }
        }
        this.sender = sender;
        this.recipients = List.copyOf(copy);
        this.timestamp = timestamp;
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public MessageMemento createMemento() {
        return new MessageMemento(content, timestamp);
    }

    public boolean matches(MessageMemento memento) {
        return memento != null
                && content.equals(memento.getContent())
                && timestamp.equals(memento.getTimestamp());
    }

    public boolean involves(User user) {
        return user != null && (sender.equals(user) || recipients.contains(user));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Message other)) {
            return false;
        }
        return timestamp.equals(other.timestamp)
                && content.equals(other.content)
                && sender.equals(other.sender)
                && recipients.equals(other.recipients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, recipients, timestamp, content);
    }

    @Override
    public String toString() {
        String names = String.join(", ", recipients.stream().map(User::getName).toList());
        return sender.getName() + " to " + names + ": " + content;
    }
}
