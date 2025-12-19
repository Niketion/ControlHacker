package com.controlhacker.bukkit.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TitleConfig {
    boolean enabled;
    String title;
    String subtitle;
    int fadeIn;
    int stay;
    int fadeOut;
}
