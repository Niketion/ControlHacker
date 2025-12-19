package com.controlhacker.bukkit;

import com.controlhacker.bukkit.command.ControlCommand;
import com.controlhacker.bukkit.config.BukkitConfig;
import com.controlhacker.bukkit.config.BukkitConfigLoader;
import com.controlhacker.bukkit.gui.FinishGuiHandler;
import com.controlhacker.bukkit.listener.ControlListener;
import com.controlhacker.bukkit.service.ControlService;
import com.controlhacker.bukkit.service.StatsService;
import com.controlhacker.bukkit.storage.StatsRepository;
import com.controlhacker.bukkit.util.AdventureAudience;
import com.controlhacker.common.StatsLedger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

@Getter
public final class ControlHackerPlugin extends JavaPlugin implements PluginMessageListener {
    public static final String CHANNEL = "controlhacker:main";

    private BukkitConfig configData;
    private StatsService statsService;
    private ControlService controlService;
    private FinishGuiHandler finishGuiHandler;
    private AdventureAudience adventureAudience;

    @Override
    public void onEnable() {
        if (adventureAudience == null) {
            adventureAudience = AdventureAudience.create(this);
        }
        reloadPlugin();

        ControlListener listener = new ControlListener(configData.getRestrictions(), controlService, finishGuiHandler);
        getServer().getPluginManager().registerEvents(listener, this);

        ControlCommand controlCommand = new ControlCommand(configData, controlService, statsService, adventureAudience);
        getCommand("control").setExecutor(controlCommand);
        getCommand("control").setTabCompleter(controlCommand);

        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
    }

    @Override
    public void onDisable() {
        if (statsService != null) {
            statsService.save();
        }
        if (finishGuiHandler != null) {
            finishGuiHandler.clear();
        }
        if (adventureAudience != null) {
            adventureAudience.close();
            adventureAudience = null;
        }
    }

    public void reloadPlugin() {
        this.configData = BukkitConfigLoader.load(this);
        StatsRepository repository = new StatsRepository(getDataFolder().toPath().resolve("stats.json"));
        this.statsService = new StatsService(repository, new StatsLedger());
        statsService.load();
        this.finishGuiHandler = new FinishGuiHandler(configData, adventureAudience);
        this.controlService = new ControlService(configData, statsService, finishGuiHandler, adventureAudience);
        this.finishGuiHandler.setControlService(controlService);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!CHANNEL.equals(channel)) {
            return;
        }
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(message))) {
            String type = input.readUTF();
            if ("START".equalsIgnoreCase(type)) {
                UUID staffId = UUID.fromString(input.readUTF());
                String staffName = input.readUTF();
                UUID targetId = UUID.fromString(input.readUTF());
                Player target = Bukkit.getPlayer(targetId);
                if (target == null) {
                    return;
                }
                controlService.startControl(staffId, staffName, target, Bukkit.getConsoleSender());
            } else if ("FINISH".equalsIgnoreCase(type)) {
                UUID staffId = UUID.fromString(input.readUTF());
                String staffName = input.readUTF();
                UUID targetId = UUID.fromString(input.readUTF());
                String actionId = input.readUTF();
                Player target = Bukkit.getPlayer(targetId);
                if (target == null) {
                    return;
                }
                var action = configData.getActions().get(actionId);
                if (action == null) {
                    return;
                }
                controlService.finishControl(staffId, staffName, target, action, Bukkit.getConsoleSender());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
