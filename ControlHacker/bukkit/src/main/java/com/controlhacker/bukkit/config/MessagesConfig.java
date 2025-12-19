package com.controlhacker.bukkit.config;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MessagesConfig {
    String prefix;
    String controlStartStaff;
    List<String> controlStartPlayer;
    String controlFinishStaff;
    String controlFinishPlayer;
    String playerNotFound;
    String alreadyControlled;
    String notControlled;
    String noPermission;
    String openFinishGui;
    String actionNotFound;
    String actionsListHeader;
    String actionLine;
    String statsHeader;
    String statsLineStarted;
    String statsLineCompleted;
    String statsLineBanned;
    String topHeader;
    String topLine;
}
