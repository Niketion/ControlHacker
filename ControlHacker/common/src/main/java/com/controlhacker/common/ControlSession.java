package com.controlhacker.common;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class ControlSession {
    UUID playerId;
    UUID staffId;
    Instant startedAt;
}
