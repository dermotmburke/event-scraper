package com.d3bot.events.models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public record Event(String artist, String location, LocalDateTime dateTime, String url) {

    public String checksum() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(url.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate checksum", e);
        }
    }

    public String key() {
        return "event:" + checksum();
    }
}
