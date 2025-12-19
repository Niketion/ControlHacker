package com.controlhacker.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class StaffStats {
    private UUID staffId;
    private String staffName;
    private int started;
    private int completed;
    private int banned;

    public StaffStats(UUID staffId, String staffName) {
        this.staffId = staffId;
        this.staffName = staffName;
    }

    public void incrementStarted() {
        started++;
    }

    public void incrementCompleted() {
        completed++;
    }

    public void incrementBanned() {
        banned++;
    }
}
