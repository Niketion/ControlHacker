package com.controlhacker.bukkit.gui;

import com.controlhacker.bukkit.config.ActionConfig;
import com.controlhacker.bukkit.config.BukkitConfig;
import com.controlhacker.bukkit.service.ControlService;
import com.controlhacker.bukkit.util.AdventureAudience;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class FinishGuiHandler {
    @Setter
    private ControlService controlService;
    private final BukkitConfig config;
    private final AdventureAudience adventureAudience;
    private final Map<FinishGuiHolder, Map<Integer, String>> guiActions = new HashMap<>();

    public void register(FinishGui gui) {
        guiActions.put(gui.getHolder(), new HashMap<>(gui.getSlots()));
    }

    public void handleClick(FinishGuiHolder holder, Player staff, int slot) {
        Map<Integer, String> slots = guiActions.get(holder);
        if (slots == null) {
            return;
        }
        String actionId = slots.get(slot);
        if (actionId == null) {
            return;
        }
        Player target = Bukkit.getPlayer(holder.getTargetId());
        if (target == null) {
            adventureAudience.send(staff, config.getMessages().getPrefix() + " " + config.getMessages().getPlayerNotFound());
            return;
        }
        ActionConfig action = config.getActions().get(actionId);
        if (action == null) {
            adventureAudience.send(staff, config.getMessages().getPrefix() + " " + config.getMessages().getActionNotFound());
            return;
        }
        if (controlService != null) {
            controlService.finishControl(staff, target, action);
        }
        staff.closeInventory();
    }

    public void clear() {
        guiActions.clear();
    }
}
