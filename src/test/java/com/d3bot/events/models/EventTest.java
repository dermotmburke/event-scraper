package com.d3bot.events.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private final Event event = new Event("Artist", "Venue", LocalDateTime.of(2026, 4, 7, 19, 0), "/url");

    @Test
    void checksumDoesNotThrow() {
        assertDoesNotThrow(event::checksum);
    }

    @Test
    void checksumIsStable() {
        assertEquals(event.checksum(), event.checksum());
    }

    @Test
    void keyHasEventPrefix() {
        assertTrue(event.key().startsWith("event:"));
    }

    @Test
    void sameEventWithDifferentParsedTimeSharesKey() {
        // The midnight-re-notification bug: Ticketmaster drops localTime,
        // extractor falls back to MIDNIGHT, but the URL is unchanged.
        var evening = new Event("Artist", "Venue", LocalDateTime.of(2026, 4, 7, 19, 0), "/url");
        var midnight = new Event("Artist", "Venue", LocalDateTime.of(2026, 4, 7, 0, 0), "/url");
        assertEquals(evening.key(), midnight.key());
    }

    @Test
    void doubleShowSameArtistSameVenueSameDayHaveDistinctKeys() {
        // Two genuine separate performances by the same artist at the same venue
        // on the same day — each has its own Ticketmaster event URL.
        var matinee = new Event("Artist", "Venue", LocalDateTime.of(2026, 4, 7, 14, 0), "/url/matinee");
        var evening = new Event("Artist", "Venue", LocalDateTime.of(2026, 4, 7, 19, 0), "/url/evening");
        assertNotEquals(matinee.key(), evening.key());
    }

    @Test
    void differentArtistsSameVenueSameDateHaveDistinctKeys() {
        var artistA = new Event("Artist A", "Venue", LocalDateTime.of(2026, 4, 7, 19, 0), "/url/a");
        var artistB = new Event("Artist B", "Venue", LocalDateTime.of(2026, 4, 7, 19, 0), "/url/b");
        assertNotEquals(artistA.key(), artistB.key());
    }
}
