package com.example.callcenter.model;

import java.util.UUID;

public class Call {
    
    private UUID uuid;
    
    public Call(UUID uuid) {
        this.uuid = uuid;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    
}
