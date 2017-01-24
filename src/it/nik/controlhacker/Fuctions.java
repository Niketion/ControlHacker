package it.nik.controlhacker;

import io.puharesource.mc.titlemanager.api.TitleObject;
import it.nik.controlhacker.files.FileLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Fuctions {
    private Main main = Main.getInstance();

    private static Fuctions instance;

    private Fuctions() {
        instance = this;
    }

    public static Fuctions getInstance() {
        if (instance == null) {
            instance = new Fuctions();
        }
        return instance;
    }

    private ArrayList<String> controlled = new ArrayList<>();
    private ArrayList<String> freezed = new ArrayList<>();

    public boolean addToControl(Player player) {
        return controlled.add(player.getName());
    }

    public boolean removeToControl(Player player) {
        return controlled.remove(player.getName());
    }

    public boolean isOnControl(Player player) {
        return controlled.contains(player.getName());
    }

    public void finishControl(Player player, CommandSender controller) {
        FileConfiguration fileConfiguration = FileLocation.getInstance().getLocationConfig();
        if (fileConfiguration.getString("EndControl.World") != null) {

            if (!isFreezed(player)) {
                player.teleport(new Location(Bukkit.getWorld(fileConfiguration.getString("EndControl.World")),
                        fileConfiguration.getInt("EndControl.X"),
                        fileConfiguration.getInt("EndControl.Y"),
                        fileConfiguration.getInt("EndControl.Z"),
                        fileConfiguration.getInt("EndControl.Yaw"),
                        fileConfiguration.getInt("EndControl.Pitch")));
            }

            removeToControl(player);
            if (isFreezed(player)) { removeToFreeze(player); }

            try {
                new TitleObject("", "").send(player);
            } catch (NoClassDefFoundError ignored) {}

            try {
                player.playSound(player.getLocation(), Sound.valueOf(main.getConfig().getString("Sound.End-Control")
                        .toUpperCase().replaceAll("-", "_")), 1, 0);
            } catch (NullPointerException e) {
                main.formatMessageError("Sound not found... Change it.");
            }

            player.sendMessage(main.formatChatPrefix(main.getConfig().getString("Chat.End-Control.Hacker")
                    .replaceAll("%player%", controller.getName())));
            controller.sendMessage(main.formatChatPrefix(main.getConfig().getString("Chat.End-Control.Staffer")
                    .replaceAll("%player%", player.getName())));
        } else {
            Bukkit.broadcastMessage(main.formatChatPrefix("&7Location of &dEndControl &7not set."));
        }
    }

    public boolean setFreeze(Player player) {
        return freezed.add(player.getName());
    }

    public boolean removeToFreeze(Player player) {
        return freezed.remove(player.getName());
    }

    public boolean isFreezed(Player player) {
        return freezed.contains(player.getName());
    }
}
