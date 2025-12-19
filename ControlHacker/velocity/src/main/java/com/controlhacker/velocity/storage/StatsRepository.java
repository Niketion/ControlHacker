package com.controlhacker.velocity.storage;

import com.controlhacker.common.StaffStats;
import com.controlhacker.common.StatsLedger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class StatsRepository {
    private static final TypeToken<Map<UUID, StaffStats>> MAP_TYPE = new TypeToken<>() {
    };

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path file;

    public void loadInto(StatsLedger ledger) {
        if (!Files.exists(file)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            Map<UUID, StaffStats> stats = gson.fromJson(reader, MAP_TYPE);
            if (stats != null) {
                ledger.putAll(stats);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveFrom(StatsLedger ledger) {
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Map<UUID, StaffStats> stats = new HashMap<>();
        for (StaffStats entry : ledger.all()) {
            stats.put(entry.getStaffId(), entry);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            gson.toJson(stats, MAP_TYPE.getType(), writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
