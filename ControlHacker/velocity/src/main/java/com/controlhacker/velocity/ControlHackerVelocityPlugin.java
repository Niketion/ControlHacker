package com.controlhacker.velocity;

import com.controlhacker.common.StatsLedger;
import com.controlhacker.velocity.command.ControlCommand;
import com.controlhacker.velocity.config.VelocityConfig;
import com.controlhacker.velocity.config.VelocityConfigLoader;
import com.controlhacker.velocity.service.ControlMessenger;
import com.controlhacker.velocity.service.ControlRegistry;
import com.controlhacker.velocity.service.StatsService;
import com.controlhacker.velocity.storage.StatsRepository;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.Getter;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "controlhacker", name = "ControlHacker", version = "2.0.0")
public final class ControlHackerVelocityPlugin {
    private static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("controlhacker:main");

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

    @Getter
    private VelocityConfig config;
    private StatsService statsService;
    private ControlRegistry registry;
    private ControlMessenger messenger;

    @Inject
    public ControlHackerVelocityPlugin(ProxyServer proxy, Logger logger, Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        reload();
        proxy.getChannelRegistrar().register(CHANNEL);

        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta meta = commandManager.metaBuilder("control")
                .aliases("controllo")
                .build();
        commandManager.register(meta, new ControlCommand(proxy, config, registry, messenger, statsService));
        logger.info("ControlHacker Velocity avviato.");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (statsService != null) {
            statsService.save();
        }
    }

    private void reload() {
        this.config = VelocityConfigLoader.load(dataDirectory);
        StatsRepository repository = new StatsRepository(dataDirectory.resolve("stats.json"));
        this.statsService = new StatsService(repository, new StatsLedger());
        statsService.load();
        this.registry = new ControlRegistry();
        this.messenger = new ControlMessenger(CHANNEL);
    }
}
