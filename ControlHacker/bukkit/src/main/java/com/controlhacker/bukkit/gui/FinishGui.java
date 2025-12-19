package com.controlhacker.bukkit.gui;

import com.controlhacker.bukkit.config.ActionConfig;
import com.controlhacker.bukkit.config.GuiConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FinishGui {
    private final FinishGuiHolder holder;
    private final Inventory inventory;
    private final Map<Integer, String> slots = new HashMap<>();

    public FinishGui(GuiConfig config, Map<String, ActionConfig> actions, UUID staffId, UUID targetId) {
        this.holder = new FinishGuiHolder(staffId, targetId);
        this.inventory = Bukkit.createInventory(holder, config.getSize(), config.getTitle());

        for (ActionConfig action : actions.values()) {
            ItemStack stack = new ItemStack(action.getMaterial());
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(action.getDisplayName());
                meta.setLore(action.getLore());
                stack.setItemMeta(meta);
            }
            int slot = action.getSlot();
            inventory.setItem(slot, stack);
            slots.put(slot, action.getId());
        }
    }
}
