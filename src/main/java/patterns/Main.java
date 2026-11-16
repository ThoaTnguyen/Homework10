package patterns;

import patterns.chat.ChatDriver;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        System.out.println("Behavioral Patterns: Chat App");
        ChatDriver.run().forEach(System.out::println);
    }
}
