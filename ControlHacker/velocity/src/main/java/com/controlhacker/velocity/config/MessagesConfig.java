package com.controlhacker.velocity.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MessagesConfig {
    String prefix;
    String playerNotFound;
    String alreadyControlled;
    String notControlled;
    String noPermission;
    String actionNotFound;
    String statsHeader;
    String statsLineStarted;
    String statsLineCompleted;
    String statsLineBanned;
    String topHeader;
    String topLine;
}
