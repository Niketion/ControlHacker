package com.controlhacker.velocity.service;

import com.controlhacker.common.StaffStats;
import com.controlhacker.common.StatsLedger;
import com.controlhacker.velocity.storage.StatsRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository repository;
    @Getter
    private final StatsLedger ledger;

    public void load() {
        repository.loadInto(ledger);
    }

    public void save() {
        repository.saveFrom(ledger);
    }

    public StaffStats getOrCreate(UUID staffId, String staffName) {
        return ledger.getOrCreate(staffId, staffName);
    }

    public List<StaffStats> top(int limit) {
        return ledger.top(limit);
    }
}
