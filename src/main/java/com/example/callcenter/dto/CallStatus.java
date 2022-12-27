package com.example.callcenter.dto;

import java.util.UUID;

public class CallStatus {
    
    private Status status;
    private UUID uuid;
    
    public CallStatus(UUID uuid, Status status) {
        this.uuid = uuid;
        this.status = status;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public Status getStatus() {
        return status;
    }
}
