package com.controlhacker.velocity.config;

import lombok.experimental.UtilityClass;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@UtilityClass
public class VelocityConfigLoader {
    public static VelocityConfig load(Path dataDirectory) {
        Path file = dataDirectory.resolve("controlhacker.conf");
        if (Files.notExists(file)) {
            try {
                Files.createDirectories(dataDirectory);
                Files.writeString(file, defaultConfig());
            } catch (IOException ex) {
                throw new IllegalStateException("Impossibile creare la config", ex);
            }
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().path(file).build();
        try {
            CommentedConfigurationNode root = loader.load();
            MessagesConfig messages = MessagesConfig.builder()
                    .prefix(root.node("messages", "prefix").getString("[Control]"))
                    .playerNotFound(root.node("messages", "player-not-found").getString("Giocatore non trovato."))
                    .alreadyControlled(root.node("messages", "already-controlled").getString("Giocatore già in controllo."))
                    .notControlled(root.node("messages", "not-controlled").getString("Giocatore non in controllo."))
                    .noPermission(root.node("messages", "no-permission").getString("Non hai il permesso."))
                    .actionNotFound(root.node("messages", "action-not-found").getString("Azione non trovata."))
                    .statsHeader(root.node("messages", "stats-header").getString("Statistiche di %staff%"))
                    .statsLineStarted(root.node("messages", "stats-line-started").getString("Controlli avviati: %value%"))
                    .statsLineCompleted(root.node("messages", "stats-line-completed").getString("Controlli conclusi: %value%"))
                    .statsLineBanned(root.node("messages", "stats-line-banned").getString("Ban eseguiti: %value%"))
                    .topHeader(root.node("messages", "top-header").getString("Top staffer"))
                    .topLine(root.node("messages", "top-line").getString("#%position% %staff% (%value%)"))
                    .build();
            String controlServer = root.node("control-server").getString("controllo");
            List<String> actions = root.node("actions").getList(String.class, List.of("ban", "release"));

            return VelocityConfig.builder()
                    .messages(messages)
                    .controlServer(controlServer)
                    .actions(actions)
                    .build();
        } catch (IOException ex) {
            throw new IllegalStateException("Impossibile caricare la config", ex);
        }
    }

    private static String defaultConfig() {
        return """
            # ControlHacker Velocity configuration
            control-server="controllo"
            actions=["ban", "release"]

            messages {
              prefix="[Control]"
              player-not-found="Giocatore non trovato."
              already-controlled="Giocatore già in controllo."
              not-controlled="Giocatore non in controllo."
              no-permission="Non hai il permesso."
              action-not-found="Azione non trovata."
              stats-header="Statistiche di %staff%"
              stats-line-started="Controlli avviati: %value%"
              stats-line-completed="Controlli conclusi: %value%"
              stats-line-banned="Ban eseguiti: %value%"
              top-header="Top staffer"
              top-line="#%position% %staff% (%value%)"
            }
            """;
    }
}
