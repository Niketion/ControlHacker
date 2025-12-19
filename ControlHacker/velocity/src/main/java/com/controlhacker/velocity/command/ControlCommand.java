package com.controlhacker.velocity.command;

import com.controlhacker.common.StaffStats;
import com.controlhacker.velocity.config.MessagesConfig;
import com.controlhacker.velocity.config.VelocityConfig;
import com.controlhacker.velocity.service.ControlMessenger;
import com.controlhacker.velocity.service.ControlRegistry;
import com.controlhacker.velocity.service.StatsService;
import com.controlhacker.velocity.util.VelocityMessages;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ControlCommand implements SimpleCommand {
    private final ProxyServer proxy;
    private final VelocityConfig config;
    private final ControlRegistry registry;
    private final ControlMessenger messenger;
    private final StatsService statsService;

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length == 0) {
            sendUsage(source);
            return;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "reload" -> handleReload(source);
            case "finish" -> handleFinish(source, args);
            case "stats" -> handleStats(source, args);
            case "top" -> handleTop(source);
            case "actions" -> handleActions(source);
            default -> handleStart(source, args[0]);
        }
    }

    private void sendUsage(CommandSource source) {
        MessagesConfig messages = config.getMessages();
        VelocityMessages.send(source, messages.getPrefix(), "/control <player>");
        VelocityMessages.send(source, messages.getPrefix(), "/control finish <player> <azione>");
        VelocityMessages.send(source, messages.getPrefix(), "/control actions");
        VelocityMessages.send(source, messages.getPrefix(), "/control stats [staff]");
        VelocityMessages.send(source, messages.getPrefix(), "/control top");
    }

    private void handleReload(CommandSource source) {
        MessagesConfig messages = config.getMessages();
        if (!source.hasPermission("controlhacker.reload")) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getNoPermission());
            return;
        }
        VelocityMessages.send(source, messages.getPrefix(), "Config ricaricata. Riavvia per applicare.");
    }

    private void handleStart(CommandSource source, String targetName) {
        MessagesConfig messages = config.getMessages();
        if (!source.hasPermission("controlhacker.control")) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getNoPermission());
            return;
        }
        Optional<Player> targetOptional = proxy.getPlayer(targetName);
        if (targetOptional.isEmpty()) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getPlayerNotFound());
            return;
        }
        Player target = targetOptional.get();
        if (registry.isControlled(target.getUniqueId())) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getAlreadyControlled());
            return;
        }
        Optional<RegisteredServer> controlServer = proxy.getServer(config.getControlServer());
        if (controlServer.isEmpty()) {
            VelocityMessages.send(source, messages.getPrefix(), "Server di controllo non trovato.");
            return;
        }
        UUID staffId = source instanceof Player player ? player.getUniqueId() : new UUID(0, 0);
        String staffName = source instanceof Player player ? player.getUsername() : "Console";

        registry.start(target.getUniqueId(), staffId);
        statsService.getOrCreate(staffId, staffName).incrementStarted();
        statsService.save();

        target.createConnectionRequest(controlServer.get()).fireAndForget();
        messenger.sendStart(target, staffId, staffName, target.getUniqueId());
        VelocityMessages.send(source, messages.getPrefix(), "Controllo avviato per " + target.getUsername() + ".");
    }

    private void handleFinish(CommandSource source, String[] args) {
        MessagesConfig messages = config.getMessages();
        if (!source.hasPermission("controlhacker.finish")) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getNoPermission());
            return;
        }
        if (args.length < 3) {
            VelocityMessages.send(source, messages.getPrefix(), "Uso: /control finish <player> <azione>");
            return;
        }
        Optional<Player> targetOptional = proxy.getPlayer(args[1]);
        if (targetOptional.isEmpty()) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getPlayerNotFound());
            return;
        }
        Player target = targetOptional.get();
        if (!registry.isControlled(target.getUniqueId())) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getNotControlled());
            return;
        }
        String actionId = args[2].toLowerCase(Locale.ROOT);
        if (!config.getActions().contains(actionId)) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getActionNotFound());
            return;
        }
        UUID staffId = source instanceof Player player ? player.getUniqueId() : new UUID(0, 0);
        String staffName = source instanceof Player player ? player.getUsername() : "Console";

        messenger.sendFinish(target, staffId, staffName, target.getUniqueId(), actionId);
        registry.end(target.getUniqueId());
        StaffStats stats = statsService.getOrCreate(staffId, staffName);
        stats.incrementCompleted();
        if ("ban".equalsIgnoreCase(actionId)) {
            stats.incrementBanned();
        }
        statsService.save();
        VelocityMessages.send(source, messages.getPrefix(), "Controllo concluso per " + target.getUsername() + ".");
    }

    private void handleStats(CommandSource source, String[] args) {
        MessagesConfig messages = config.getMessages();
        if (!source.hasPermission("controlhacker.stats")) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getNoPermission());
            return;
        }
        UUID staffId;
        String staffName;
        if (args.length >= 2) {
            Optional<Player> player = proxy.getPlayer(args[1]);
            if (player.isEmpty()) {
                VelocityMessages.send(source, messages.getPrefix(), messages.getPlayerNotFound());
                return;
            }
            staffId = player.get().getUniqueId();
            staffName = player.get().getUsername();
        } else if (source instanceof Player player) {
            staffId = player.getUniqueId();
            staffName = player.getUsername();
        } else {
            VelocityMessages.send(source, messages.getPrefix(), "Specifica uno staffer.");
            return;
        }
        StaffStats stats = statsService.getOrCreate(staffId, staffName);
        VelocityMessages.send(source, messages.getPrefix(),
                messages.getStatsHeader().replace("%staff%", staffName));
        VelocityMessages.send(source, messages.getPrefix(),
                messages.getStatsLineStarted().replace("%value%", String.valueOf(stats.getStarted())));
        VelocityMessages.send(source, messages.getPrefix(),
                messages.getStatsLineCompleted().replace("%value%", String.valueOf(stats.getCompleted())));
        VelocityMessages.send(source, messages.getPrefix(),
                messages.getStatsLineBanned().replace("%value%", String.valueOf(stats.getBanned())));
    }

    private void handleTop(CommandSource source) {
        MessagesConfig messages = config.getMessages();
        if (!source.hasPermission("controlhacker.top")) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getNoPermission());
            return;
        }
        VelocityMessages.send(source, messages.getPrefix(), messages.getTopHeader());
        int position = 1;
        for (StaffStats stats : statsService.top(10)) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getTopLine()
                    .replace("%position%", String.valueOf(position))
                    .replace("%staff%", stats.getStaffName())
                    .replace("%value%", String.valueOf(stats.getCompleted())));
            position++;
        }
    }

    private void handleActions(CommandSource source) {
        MessagesConfig messages = config.getMessages();
        if (!source.hasPermission("controlhacker.finish")) {
            VelocityMessages.send(source, messages.getPrefix(), messages.getNoPermission());
            return;
        }
        VelocityMessages.send(source, messages.getPrefix(), "Azioni: " + String.join(", ", config.getActions()));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) {
            return List.of("finish", "stats", "top", "actions", "reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("finish")) {
            return proxy.getAllPlayers().stream().map(Player::getUsername).toList();
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("finish")) {
            return config.getActions();
        }
        return List.of();
    }
}
