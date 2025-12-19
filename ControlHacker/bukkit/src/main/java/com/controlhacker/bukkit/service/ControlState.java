package com.controlhacker.bukkit.service;

import com.controlhacker.common.ControlSession;
import lombok.Builder;
import lombok.Value;
import org.bukkit.GameMode;
import org.bukkit.Location;

@Value
@Builder
public class ControlState {
    ControlSession session;
    String staffName;
    String playerName;
    Location previousLocation;
    GameMode previousGameMode;
}
