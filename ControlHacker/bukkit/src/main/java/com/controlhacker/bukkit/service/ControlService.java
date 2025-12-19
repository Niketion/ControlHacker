package com.controlhacker.bukkit.service;

import com.controlhacker.bukkit.config.ActionConfig;
import com.controlhacker.bukkit.config.BukkitConfig;
import com.controlhacker.bukkit.config.MessagesConfig;
import com.controlhacker.bukkit.gui.FinishGui;
import com.controlhacker.bukkit.util.PlaceholderUtil;
import com.controlhacker.bukkit.util.AdventureUtil;
import com.controlhacker.bukkit.util.AdventureAudience;
import com.controlhacker.common.ControlActionType;
import com.controlhacker.common.ControlSession;
import com.controlhacker.common.StaffStats;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.title.Title;
import java.time.Duration;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ControlService {
    private final BukkitConfig config;
    private final StatsService statsService;
    private final com.controlhacker.bukkit.gui.FinishGuiHandler guiHandler;
    private final AdventureAudience adventureAudience;
    private final Map<UUID, ControlState> sessions = new HashMap<>();

    public Map<UUID, ControlState> getSessions() {
        return Collections.unmodifiableMap(sessions);
    }

    public boolean isControlled(UUID playerId) {
        return sessions.containsKey(playerId);
    }

    public Optional<ControlState> getSession(UUID playerId) {
        return Optional.ofNullable(sessions.get(playerId));
    }

    public boolean startControl(CommandSender staffSender, Player target) {
        UUID staffId = staffSender instanceof Player player ? player.getUniqueId() : new UUID(0, 0);
        String staffName = staffSender.getName();
        return startControl(staffId, staffName, target, staffSender);
    }

    public boolean startControl(UUID staffId, String staffName, Player target, CommandSender feedbackTarget) {
        if (sessions.containsKey(target.getUniqueId())) {
            return false;
        }
        Location controlLocation = config.getControlZone().toLocation();
        if (controlLocation.getWorld() == null) {
            adventureAudience.send(feedbackTarget, config.getMessages().getPrefix() + " &cMondo di controllo non valido.");
            return false;
        }

        ControlState state = ControlState.builder()
                .session(ControlSession.builder()
                        .playerId(target.getUniqueId())
                        .staffId(staffId)
                        .startedAt(Instant.now())
                        .build())
                .staffName(staffName)
                .playerName(target.getName())
                .previousLocation(target.getLocation().clone())
                .previousGameMode(target.getGameMode())
                .build();
        sessions.put(target.getUniqueId(), state);

        StaffStats stats = statsService.getOrCreate(staffId, staffName);
        stats.incrementStarted();
        statsService.save();

        target.teleport(controlLocation);
        target.setGameMode(GameMode.ADVENTURE);

        runCommands(config.getStartCommands(), staffName, target.getName(), feedbackTarget);
        sendControlStartMessages(staffName, target);
        return true;
    }

    public boolean finishControl(CommandSender staffSender, Player target, ActionConfig action) {
        if (staffSender instanceof Player player) {
            return finishControl(player.getUniqueId(), player.getName(), target, action, staffSender);
        }
        ControlState state = sessions.get(target.getUniqueId());
        if (state == null) {
            return false;
        }
        return finishControl(state.getSession().getStaffId(), state.getStaffName(), target, action, staffSender);
    }

    public boolean finishControl(UUID staffId, String staffName, Player target, ActionConfig action,
                                 CommandSender commandSender) {
        ControlState state = sessions.get(target.getUniqueId());
        if (state == null) {
            return false;
        }

        runActionCommands(action, staffName, target.getName(), commandSender);
        endControlSession(target, state, action.getType());

        StaffStats stats = statsService.getOrCreate(staffId, staffName);
        stats.incrementCompleted();
        if (action.getType() == ControlActionType.BAN) {
            stats.incrementBanned();
        }
        statsService.save();

        MessagesConfig messages = config.getMessages();
        if (commandSender != null) {
            adventureAudience.send(commandSender, messages.getPrefix() + " "
                    + PlaceholderUtil.apply(messages.getControlFinishStaff(), target.getName(), staffName));
        }
        adventureAudience.send(target, messages.getPrefix() + " "
                + PlaceholderUtil.apply(messages.getControlFinishPlayer(), target.getName(), staffName));
        return true;
    }

    public void handleQuit(Player player) {
        ControlState state = sessions.remove(player.getUniqueId());
        if (state == null) {
            return;
        }
        runCommands(config.getQuitCommands(), state.getStaffName(), state.getPlayerName(), null);
    }

    public void openFinishGui(Player staff, Player target) {
        FinishGui gui = new FinishGui(config.getGui(), config.getActions(), staff.getUniqueId(), target.getUniqueId());
        guiHandler.register(gui);
        staff.openInventory(gui.getInventory());
        adventureAudience.send(staff, config.getMessages().getPrefix() + " " + config.getMessages().getOpenFinishGui());
    }

    private void sendControlStartMessages(String staffName, Player target) {
        MessagesConfig messages = config.getMessages();
        if (config.getTitle().isEnabled()) {
            String titleText = PlaceholderUtil.apply(config.getTitle().getTitle(), target.getName(), staffName);
            String subtitleText = PlaceholderUtil.apply(config.getTitle().getSubtitle(), target.getName(), staffName);
            Title title = Title.title(
                    AdventureUtil.component(titleText),
                    AdventureUtil.component(subtitleText),
                    Title.Times.times(
                            Duration.ofTicks(config.getTitle().getFadeIn()),
                            Duration.ofTicks(config.getTitle().getStay()),
                            Duration.ofTicks(config.getTitle().getFadeOut())
                    )
            );
            target.showTitle(title);
        }
        String staffMessage = PlaceholderUtil.apply(messages.getControlStartStaff(), target.getName(), staffName);
        String playerMessagePrefix = messages.getPrefix() + " ";
        if (staffMessage != null && !staffMessage.isEmpty()) {
            Player staff = Bukkit.getPlayerExact(staffName);
            if (staff != null) {
                adventureAudience.send(staff, playerMessagePrefix + staffMessage);
            }
        }
        for (String line : messages.getControlStartPlayer()) {
            adventureAudience.send(target, playerMessagePrefix + PlaceholderUtil.apply(line, target.getName(), staffName));
        }
    }

    private void runActionCommands(ActionConfig action, String staffName, String playerName,
                                   CommandSender staffSender) {
        runCommands(action.getCommands(), staffName, playerName, action.getExecutor() == CommandExecutorType.STAFF
                ? staffSender
                : Bukkit.getConsoleSender());
    }

    private void runCommands(Iterable<String> commands, String staffName, String playerName, CommandSender executor) {
        if (commands == null) {
            return;
        }
        CommandSender sender = executor == null ? Bukkit.getConsoleSender() : executor;
        for (String command : commands) {
            if (command == null || command.isBlank()) {
                continue;
            }
            String parsed = PlaceholderUtil.apply(command, playerName, staffName);
            Bukkit.dispatchCommand(sender, parsed);
        }
    }

    private void endControlSession(Player player, ControlState state, ControlActionType actionType) {
        sessions.remove(player.getUniqueId());
        player.teleport(state.getPreviousLocation());
        player.setGameMode(state.getPreviousGameMode());
        if (actionType == ControlActionType.BAN) {
            player.kickPlayer("Controllo terminato.");
        }
    }
}
