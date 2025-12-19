package com.controlhacker.velocity.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class VelocityConfig {
    MessagesConfig messages;
    String controlServer;
    List<String> actions;
}
