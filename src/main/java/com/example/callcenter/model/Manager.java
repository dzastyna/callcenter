package com.example.callcenter.model;

public class Manager extends Employee {
    @Override
    public boolean canHandleRequest(CallerRequestLevel level) {
        if (level == null)
            throw new IllegalArgumentException();
        
        return CallerRequestLevel.DIRECTOR != level;
    }

    @Override
    public CallerRequestLevel getLevel() {
        return CallerRequestLevel.MANAGER;
    }
}
