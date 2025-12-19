package com.controlhacker.bukkit.config;

import com.controlhacker.bukkit.service.CommandExecutorType;
import com.controlhacker.bukkit.util.ColorUtil;
import com.controlhacker.common.ControlActionType;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class BukkitConfigLoader {
    public static BukkitConfig load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        MessagesConfig messages = MessagesConfig.builder()
                .prefix(color(config.getString("messages.prefix", "&c[Control]")))
                .controlStartStaff(color(config.getString("messages.control-start-staff", "&aControllo avviato.")))
                .controlStartPlayer(colorList(config.getStringList("messages.control-start-player")))
                .controlFinishStaff(color(config.getString("messages.control-finish-staff", "&aControllo terminato.")))
                .controlFinishPlayer(color(config.getString("messages.control-finish-player", "&aControllo terminato.")))
                .playerNotFound(color(config.getString("messages.player-not-found", "&cGiocatore non trovato.")))
                .alreadyControlled(color(config.getString("messages.already-controlled", "&cGiocatore già in controllo.")))
                .notControlled(color(config.getString("messages.not-controlled", "&cGiocatore non in controllo.")))
                .noPermission(color(config.getString("messages.no-permission", "&cNon hai il permesso.")))
                .openFinishGui(color(config.getString("messages.open-finish-gui", "&7Scegli un'azione.")))
                .actionNotFound(color(config.getString("messages.action-not-found", "&cAzione non trovata.")))
                .actionsListHeader(color(config.getString("messages.actions-list-header", "&eAzioni disponibili:")))
                .actionLine(color(config.getString("messages.action-line", "&7- %id%: %name%")))
                .statsHeader(color(config.getString("messages.stats-header", "&eStatistiche di %staff%")))
                .statsLineStarted(color(config.getString("messages.stats-line-started", "&7Controlli avviati: &f%value%")))
                .statsLineCompleted(color(config.getString("messages.stats-line-completed", "&7Controlli conclusi: &f%value%")))
                .statsLineBanned(color(config.getString("messages.stats-line-banned", "&7Ban eseguiti: &f%value%")))
                .topHeader(color(config.getString("messages.top-header", "&eTop staffer")))
                .topLine(color(config.getString("messages.top-line", "&7#%position% &f%staff% &7(%value%)")))
                .build();

        ConfigurationSection control = config.getConfigurationSection("control");
        ControlZone zone = ControlZone.builder()
                .world(control != null ? control.getString("world", "world") : "world")
                .x(control != null ? control.getDouble("x", 0) : 0)
                .y(control != null ? control.getDouble("y", 80) : 80)
                .z(control != null ? control.getDouble("z", 0) : 0)
                .yaw(control != null ? (float) control.getDouble("yaw", 0) : 0f)
                .pitch(control != null ? (float) control.getDouble("pitch", 0) : 0f)
                .build();

        ConfigurationSection restrictions = config.getConfigurationSection("restrictions");
        RestrictionsConfig restrictionsConfig = RestrictionsConfig.builder()
                .stopMove(restrictions == null || restrictions.getBoolean("stop-move", true))
                .stopCommand(restrictions == null || restrictions.getBoolean("stop-command", true))
                .stopChat(restrictions == null || restrictions.getBoolean("stop-chat", true))
                .stopDrop(restrictions == null || restrictions.getBoolean("stop-drop", true))
                .stopBreak(restrictions == null || restrictions.getBoolean("stop-break", true))
                .stopPlace(restrictions == null || restrictions.getBoolean("stop-place", true))
                .stopDamage(restrictions == null || restrictions.getBoolean("stop-damage", true))
                .stopInteract(restrictions == null || restrictions.getBoolean("stop-interact", true))
                .commandWhitelist(restrictions == null
                        ? Collections.emptyList()
                        : restrictions.getStringList("command-whitelist"))
                .build();

        GuiConfig gui = GuiConfig.builder()
                .title(color(config.getString("gui.title", "&c&lAzioni controllo")))
                .size(config.getInt("gui.size", 27))
                .build();

        ConfigurationSection titleSection = config.getConfigurationSection("title");
        TitleConfig title = TitleConfig.builder()
                .enabled(titleSection == null || titleSection.getBoolean("enabled", true))
                .title(color(titleSection != null ? titleSection.getString("title", "&cCONTROL") : "&cCONTROL"))
                .subtitle(color(titleSection != null ? titleSection.getString("subtitle", "&7by %staff%") : "&7by %staff%"))
                .fadeIn(titleSection != null ? titleSection.getInt("fade-in", 10) : 10)
                .stay(titleSection != null ? titleSection.getInt("stay", 60) : 60)
                .fadeOut(titleSection != null ? titleSection.getInt("fade-out", 10) : 10)
                .build();

        Map<String, ActionConfig> actions = new HashMap<>();
        ConfigurationSection actionsSection = config.getConfigurationSection("actions");
        if (actionsSection != null) {
            for (String id : actionsSection.getKeys(false)) {
                ConfigurationSection actionSection = actionsSection.getConfigurationSection(id);
                if (actionSection == null) {
                    continue;
                }
                String display = color(actionSection.getString("display-name", id));
                List<String> lore = colorList(actionSection.getStringList("lore"));
                Material material = Material.matchMaterial(actionSection.getString("material", "PAPER"));
                if (material == null) {
                    material = Material.PAPER;
                }
                int slot = actionSection.getInt("slot", 0);
                ControlActionType type = parseEnum(ControlActionType.class, actionSection.getString("type"),
                        ControlActionType.OTHER);
                CommandExecutorType executor = parseEnum(CommandExecutorType.class, actionSection.getString("executor"),
                        CommandExecutorType.CONSOLE);
                List<String> commands = actionSection.getStringList("commands");
                actions.put(id, ActionConfig.builder()
                        .id(id)
                        .displayName(display)
                        .lore(lore)
                        .material(material)
                        .slot(slot)
                        .type(type)
                        .executor(executor)
                        .commands(commands)
                        .build());
            }
        }

        return BukkitConfig.builder()
                .messages(messages)
                .controlZone(zone)
                .restrictions(restrictionsConfig)
                .gui(gui)
                .title(title)
                .actions(actions)
                .startCommands(config.getStringList("control.start-commands"))
                .quitCommands(config.getStringList("control.quit-commands"))
                .build();
    }

    private static String color(String value) {
        return ColorUtil.colorize(value);
    }

    private static List<String> colorList(List<String> values) {
        return ColorUtil.colorize(values);
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> enumType, String value, T fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }
}
