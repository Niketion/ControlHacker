package com.controlhacker.bukkit.listener;

import com.controlhacker.bukkit.config.RestrictionsConfig;
import com.controlhacker.bukkit.gui.FinishGuiHandler;
import com.controlhacker.bukkit.gui.FinishGuiHolder;
import com.controlhacker.bukkit.service.ControlService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class ControlListener implements Listener {
    private final RestrictionsConfig restrictions;
    private final ControlService controlService;
    private final FinishGuiHandler guiHandler;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!restrictions.isStopMove()) {
            return;
        }
        Player player = event.getPlayer();
        if (!controlService.isControlled(player.getUniqueId())) {
            return;
        }
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!restrictions.isStopCommand()) {
            return;
        }
        Player player = event.getPlayer();
        if (!controlService.isControlled(player.getUniqueId())) {
            return;
        }
        String command = event.getMessage().split(" ")[0].toLowerCase();
        for (String whitelisted : restrictions.getCommandWhitelist()) {
            if (command.equalsIgnoreCase(whitelisted)) {
                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!restrictions.isStopChat()) {
            return;
        }
        if (controlService.isControlled(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!restrictions.isStopDrop()) {
            return;
        }
        if (controlService.isControlled(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!restrictions.isStopBreak()) {
            return;
        }
        if (controlService.isControlled(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!restrictions.isStopPlace()) {
            return;
        }
        if (controlService.isControlled(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!restrictions.isStopDamage()) {
            return;
        }
        if (event.getEntity() instanceof Player player
                && controlService.isControlled(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!restrictions.isStopInteract()) {
            return;
        }
        if (controlService.isControlled(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        controlService.handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onFinishClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof FinishGuiHolder holder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player staff)) {
            return;
        }
        guiHandler.handleClick(holder, staff, event.getSlot());
    }
}
