package com.example.careeradvisor.dto;

import com.example.careeradvisor.entity.ProgressStatus;
import jakarta.validation.constraints.NotNull;

public class ProgressUpdateRequest {

    @NotNull(message = "Progress status is required (NOT_STARTED, IN_PROGRESS, COMPLETED)")
    private ProgressStatus status;

    public ProgressUpdateRequest() {
    }

    public ProgressUpdateRequest(ProgressStatus status) {
        this.status = status;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
    }
}
