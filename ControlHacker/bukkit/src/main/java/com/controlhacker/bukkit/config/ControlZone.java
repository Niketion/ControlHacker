package com.controlhacker.bukkit.config;

import lombok.Builder;
import lombok.Value;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Value
@Builder
public class ControlZone {
    String world;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
