package com.controlhacker.bukkit.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class BukkitConfig {
    MessagesConfig messages;
    ControlZone controlZone;
    RestrictionsConfig restrictions;
    GuiConfig gui;
    TitleConfig title;
    Map<String, ActionConfig> actions;
    List<String> startCommands;
    List<String> quitCommands;
}
