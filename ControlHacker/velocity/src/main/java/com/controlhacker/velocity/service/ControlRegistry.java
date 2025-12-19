package com.controlhacker.velocity.service;

import com.controlhacker.common.ControlSession;
import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class ControlRegistry {
    private final Map<UUID, ControlSession> sessions = new HashMap<>();

    public boolean isControlled(UUID playerId) {
        return sessions.containsKey(playerId);
    }

    public void start(UUID playerId, UUID staffId) {
        sessions.put(playerId, ControlSession.builder()
                .playerId(playerId)
                .staffId(staffId)
                .startedAt(Instant.now())
                .build());
    }

    public Optional<ControlSession> end(UUID playerId) {
        return Optional.ofNullable(sessions.remove(playerId));
    }
}
