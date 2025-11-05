package com.scar.lms.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private String profilePictureUrl;
    private MessageType type;

    @SuppressWarnings("unused")
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    public void setType(MessageType type) {
        this.type = type;
    }
    public String getContent() {
        return content;
    }
    public String getSender() {
        return sender;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    public MessageType getType() {
        return type;
    }
}