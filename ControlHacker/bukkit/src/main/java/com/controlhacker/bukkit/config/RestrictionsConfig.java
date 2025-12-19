package com.controlhacker.bukkit.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestrictionsConfig {
    boolean stopMove;
    boolean stopCommand;
    boolean stopChat;
    boolean stopDrop;
    boolean stopBreak;
    boolean stopPlace;
    boolean stopDamage;
    boolean stopInteract;
    List<String> commandWhitelist;
}
