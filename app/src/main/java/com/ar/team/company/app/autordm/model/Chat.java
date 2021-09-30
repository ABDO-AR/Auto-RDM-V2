package com.ar.team.company.app.autordm.model;

import java.util.List;

@SuppressWarnings("unused")
public class Chat {

    // Fields:
    private final String sender;
    private final String chatDate;
    private final List<Messages> messages;
    // Checking:
    private boolean isHasNewMessage = false;
    private boolean isNewMessage = false;

    // Constructor:
    public Chat(String sender, String chatDate, List<Messages> messages) {
        this.sender = sender;
        this.chatDate = chatDate;
        this.messages = messages;
    }

    // InnerClasses:
    public static class Messages {

        // Fields:
        private final String message;
        private final String messageDate;
        private final boolean sender;

        // Constructor:
        public Messages(String message, String messageDate, boolean sender ) {
            this.message = message;
            this.messageDate = messageDate;
            this.sender = sender;

        }

        // Getters:
        public String getMessage() {
            return message;
        }

        public String getMessageDate() {
            return messageDate;
        }

        public boolean isSender() {
            return sender;
        }
    }

    // Checking(Getter(&Setter)):
    public boolean isNewMessage() {
        return isNewMessage;
    }

    public void setNewMessage(boolean newMessage) {
        isNewMessage = newMessage;
    }

    public boolean isHasNewMessage() {
        return isHasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        isHasNewMessage = hasNewMessage;
    }

    // Getters:
    public String getSender() {
        return sender;
    }

    public String getChatDate() {
        return chatDate;
    }

    public List<Messages> getMessages() {
        return messages;
    }

}
