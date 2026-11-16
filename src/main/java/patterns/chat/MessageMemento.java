package patterns.chat;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Memento. A snapshot of a sent message's content and timestamp.
 * The caretaker can undo that send without the message exposing its fields for editing.
 */
public class MessageMemento {
    private final String content;
    private final LocalDateTime timestamp;

    public MessageMemento(String content, LocalDateTime timestamp) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("message content is required");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp is required");
        }
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MessageMemento other)) {
            return false;
        }
        return content.equals(other.content) && timestamp.equals(other.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, timestamp);
    }

    @Override
    public String toString() {
        return content + " @ " + timestamp;
    }
}
