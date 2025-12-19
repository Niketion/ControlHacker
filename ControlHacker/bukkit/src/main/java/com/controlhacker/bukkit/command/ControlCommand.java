package com.controlhacker.bukkit.command;

import com.controlhacker.bukkit.config.ActionConfig;
import com.controlhacker.bukkit.config.BukkitConfig;
import com.controlhacker.bukkit.config.MessagesConfig;
import com.controlhacker.bukkit.service.ControlService;
import com.controlhacker.bukkit.service.StatsService;
import com.controlhacker.bukkit.util.ColorUtil;
import com.controlhacker.bukkit.util.AdventureAudience;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
public class ControlCommand implements CommandExecutor, TabCompleter {
    private final BukkitConfig config;
    private final ControlService controlService;
    private final StatsService statsService;
    private final AdventureAudience adventureAudience;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        return switch (sub) {
            case "finish" -> handleFinish(sender, args);
            case "stats" -> handleStats(sender, args);
            case "top" -> handleTop(sender);
            case "actions" -> handleActions(sender);
            case "reload" -> handleReload(sender);
            default -> handleStart(sender, args[0]);
        };
    }

    private void sendUsage(CommandSender sender) {
        adventureAudience.send(sender, ColorUtil.colorize("&e/control <player>"));
        adventureAudience.send(sender, ColorUtil.colorize("&e/control finish <player> [azione]"));
        adventureAudience.send(sender, ColorUtil.colorize("&e/control actions"));
        adventureAudience.send(sender, ColorUtil.colorize("&e/control stats [staff]"));
        adventureAudience.send(sender, ColorUtil.colorize("&e/control top"));
        adventureAudience.send(sender, ColorUtil.colorize("&e/control reload"));
    }

    private boolean handleStart(CommandSender sender, String targetName) {
        if (!sender.hasPermission("controlhacker.control")) {
            adventureAudience.send(sender, config.getMessages().getPrefix() + " " + config.getMessages().getNoPermission());
            return true;
        }
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            adventureAudience.send(sender, config.getMessages().getPrefix() + " " + config.getMessages().getPlayerNotFound());
            return true;
        }
        if (sender instanceof Player player && player.getUniqueId().equals(target.getUniqueId())) {
            adventureAudience.send(sender, config.getMessages().getPrefix() + " &cNon puoi controllare te stesso.");
            return true;
        }
        boolean started = controlService.startControl(sender, target);
        if (!started) {
            adventureAudience.send(sender, config.getMessages().getPrefix() + " "
                    + config.getMessages().getAlreadyControlled().replace("%player%", target.getName()));
        }
        return true;
    }

    private boolean handleFinish(CommandSender sender, String[] args) {
        if (!sender.hasPermission("controlhacker.finish")) {
            adventureAudience.send(sender, config.getMessages().getPrefix() + " " + config.getMessages().getNoPermission());
            return true;
        }
        if (args.length < 2) {
            adventureAudience.send(sender, ColorUtil.colorize("&cUso: /control finish <player> [azione]"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            adventureAudience.send(sender, config.getMessages().getPrefix() + " " + config.getMessages().getPlayerNotFound());
            return true;
        }
        if (!controlService.isControlled(target.getUniqueId())) {
            adventureAudience.send(sender, config.getMessages().getPrefix() + " " + config.getMessages().getNotControlled());
            return true;
        }
        if (args.length == 2) {
            if (sender instanceof Player player) {
                controlService.openFinishGui(player, target);
            } else {
                adventureAudience.send(sender, ColorUtil.colorize("&cSolo i player possono aprire la GUI."));
            }
            return true;
        }
        String actionId = args[2].toLowerCase(Locale.ROOT);
        ActionConfig action = config.getActions().get(actionId);
        if (action == null) {
            adventureAudience.send(sender, config.getMessages().getPrefix() + " " + config.getMessages().getActionNotFound());
            return true;
        }
        controlService.finishControl(sender, target, action);
        return true;
    }

    private boolean handleStats(CommandSender sender, String[] args) {
        MessagesConfig messages = config.getMessages();
        if (!sender.hasPermission("controlhacker.stats")) {
            adventureAudience.send(sender, messages.getPrefix() + " " + messages.getNoPermission());
            return true;
        }
        UUID staffId;
        String staffName;
        if (args.length >= 2) {
            staffName = args[1];
            Player staff = Bukkit.getPlayerExact(staffName);
            if (staff != null) {
                staffId = staff.getUniqueId();
                staffName = staff.getName();
            } else {
                adventureAudience.send(sender, messages.getPrefix() + " " + messages.getPlayerNotFound());
                return true;
            }
        } else if (sender instanceof Player player) {
            staffId = player.getUniqueId();
            staffName = player.getName();
        } else {
            adventureAudience.send(sender, ColorUtil.colorize("&cSpecifica un giocatore."));
            return true;
        }
        sendStats(sender, staffId, staffName);
        return true;
    }

    private void sendStats(CommandSender sender, UUID staffId, String staffName) {
        MessagesConfig messages = config.getMessages();
        var stats = statsService.getOrCreate(staffId, staffName);
        adventureAudience.send(sender, messages.getPrefix() + " "
                + messages.getStatsHeader().replace("%staff%", staffName));
        adventureAudience.send(sender, messages.getPrefix() + " "
                + messages.getStatsLineStarted().replace("%value%", String.valueOf(stats.getStarted())));
        adventureAudience.send(sender, messages.getPrefix() + " "
                + messages.getStatsLineCompleted().replace("%value%", String.valueOf(stats.getCompleted())));
        adventureAudience.send(sender, messages.getPrefix() + " "
                + messages.getStatsLineBanned().replace("%value%", String.valueOf(stats.getBanned())));
    }

    private boolean handleTop(CommandSender sender) {
        MessagesConfig messages = config.getMessages();
        if (!sender.hasPermission("controlhacker.top")) {
            adventureAudience.send(sender, messages.getPrefix() + " " + messages.getNoPermission());
            return true;
        }
        adventureAudience.send(sender, messages.getPrefix() + " " + messages.getTopHeader());
        int position = 1;
        for (var stats : statsService.top(10)) {
            String line = messages.getTopLine()
                    .replace("%position%", String.valueOf(position))
                    .replace("%staff%", stats.getStaffName())
                    .replace("%value%", String.valueOf(stats.getCompleted()));
            adventureAudience.send(sender, messages.getPrefix() + " " + line);
            position++;
        }
        return true;
    }

    private boolean handleActions(CommandSender sender) {
        MessagesConfig messages = config.getMessages();
        if (!sender.hasPermission("controlhacker.finish")) {
            adventureAudience.send(sender, messages.getPrefix() + " " + messages.getNoPermission());
            return true;
        }
        adventureAudience.send(sender, messages.getPrefix() + " " + messages.getActionsListHeader());
        config.getActions().forEach((id, action) -> {
            String line = messages.getActionLine()
                    .replace("%id%", id)
                    .replace("%name%", action.getDisplayName());
            adventureAudience.send(sender, messages.getPrefix() + " " + line);
        });
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        MessagesConfig messages = config.getMessages();
        if (!sender.hasPermission("controlhacker.reload")) {
            adventureAudience.send(sender, messages.getPrefix() + " " + messages.getNoPermission());
            return true;
        }
        adventureAudience.send(sender, messages.getPrefix() + " &aConfigurazione ricaricata. Riavvia il plugin.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("finish");
            completions.add("stats");
            completions.add("top");
            completions.add("actions");
            completions.add("reload");
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("finish")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("finish")) {
            return new ArrayList<>(config.getActions().keySet());
        }
        return Collections.emptyList();
    }
}
