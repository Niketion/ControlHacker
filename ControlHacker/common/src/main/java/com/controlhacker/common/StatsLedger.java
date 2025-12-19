package com.controlhacker.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatsLedger {
    @Getter
    private final Map<UUID, StaffStats> stats = new LinkedHashMap<>();

    public StaffStats getOrCreate(UUID staffId, String staffName) {
        return stats.computeIfAbsent(staffId, id -> new StaffStats(id, staffName));
    }

    public Collection<StaffStats> all() {
        return stats.values();
    }

    public List<StaffStats> top(int limit) {
        List<StaffStats> entries = new ArrayList<>(stats.values());
        entries.sort(Comparator.comparingInt(StaffStats::getCompleted).reversed()
                .thenComparingInt(StaffStats::getStarted).reversed()
                .thenComparing(StaffStats::getStaffName, String.CASE_INSENSITIVE_ORDER));
        if (entries.size() <= limit) {
            return entries;
        }
        return entries.subList(0, limit);
    }

    public void putAll(Map<UUID, StaffStats> entries) {
        stats.clear();
        stats.putAll(entries);
    }
}
