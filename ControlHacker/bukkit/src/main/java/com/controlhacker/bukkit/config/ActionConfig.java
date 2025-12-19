package com.controlhacker.bukkit.config;

import com.controlhacker.bukkit.service.CommandExecutorType;
import com.controlhacker.common.ControlActionType;
import lombok.Builder;
import lombok.Value;
import org.bukkit.Material;

import java.util.List;

@Value
@Builder
public class ActionConfig {
    String id;
    String displayName;
    List<String> lore;
    Material material;
    int slot;
    ControlActionType type;
    CommandExecutorType executor;
    List<String> commands;
}
