package com.controlhacker.bukkit.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GuiConfig {
    String title;
    int size;
}
