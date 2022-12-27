package com.example.callcenter.model;

import java.util.Queue;
import java.util.UUID;

public abstract class Employee {
    
    private Call activeCall;
    
    public boolean isFree() {
        return !inCall();
    }
    
    public UUID getActiveCallUuid() {
        return activeCall == null ? null : activeCall.getUuid();
    }
    
    public void handleCall(Call call) {
        if (inCall()) {
            throw new IllegalStateException(
                "Employee " + this + " cannot handle this call, is in call " + call.getUuid());
        }
        
        activeCall = call;
    }
    
    public boolean canHandleCall() {
        return !inCall();
    }
    
    public boolean canHandleRequest(CallerRequestLevel level) {
        if (level == null)
            throw new IllegalArgumentException();
        
        return false;
        
    }

    private boolean inCall() {
        return activeCall != null;
    }
    
    public boolean isInCall(UUID uuid) {
        return inCall() && uuid.equals(activeCall.getUuid());
    }
    
    public void endCall() {
        if (activeCall == null)
            throw new IllegalStateException("Cannot end call, because is not in call");
        
        this.activeCall = null;
    }
    
    public void passCall(Employee employee) {
        employee.handleCall(activeCall);
        activeCall = null;
    }

    public abstract CallerRequestLevel getLevel();

    public void passCall(Queue<Call> callQueue) {
        callQueue.add(activeCall);
        activeCall = null;
    }
}
