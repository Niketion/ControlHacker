package com.controlhacker.bukkit.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class FinishGuiHolder implements InventoryHolder {
    private final UUID staffId;
    private final UUID targetId;

    @Override
    public Inventory getInventory() {
        return null;
    }
}
