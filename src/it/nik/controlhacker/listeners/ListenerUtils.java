package it.nik.controlhacker.listeners;

import it.nik.controlhacker.Fuctions;
import it.nik.controlhacker.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

public class ListenerUtils implements Listener {
    private Main main = Main.getInstance();

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isOnControl(player)) {
            event.setCancelled(true);
            player.sendMessage(main.formatChatPrefix(getConfigString("Event.Command-Blocked")));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isOnControl(event.getPlayer()) || isFreezed(event.getPlayer())) {
            for (Player staffer : Bukkit.getServer().getOnlinePlayers()) {
                if (staffer.hasPermission("controlhacker.mod")) {
                    staffer.sendMessage(main.formatChatPrefix(main.getConfig().getString("Chat.Quit")).replaceAll("%player%", event
                            .getPlayer().getName()));
                    try {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf(main.getConfig().getString("Sound.Start-Control")
                                .toUpperCase().replaceAll("-", "_")), 1, 0);
                    } catch (NullPointerException ignored) {
                    }
                    Fuctions.getInstance().finishControl(event.getPlayer(), Bukkit.getConsoleSender());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (isOnControl(event.getPlayer())) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(getConfigBoolean("Event.Block-Break"));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (isFreezed(event.getPlayer())) {
            event.setTo(event.getFrom());
            event.getPlayer().sendMessage(main.formatChat(main.getConfig().getString("Freeze.Message")));
        }
    }

    private boolean isOnControl(Player player) {
        return Fuctions.getInstance().isOnControl(player);
    }
    private boolean isFreezed(Player player) {
        return Fuctions.getInstance().isFreezed(player);
    }

    private String getConfigString(String string) {
        return main.getConfig().getString(string);
    }

    private boolean getConfigBoolean(String string) {
        return main.getConfig().getBoolean(string);
    }
}
