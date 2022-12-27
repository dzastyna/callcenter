package com.example.callcenter;

import com.example.callcenter.dto.CallStatus;
import com.example.callcenter.model.CallCenter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CallCenterService {
    
    private CallCenter callCenter = new CallCenter(3, 2);
    
    public UUID handleCall() {
        return callCenter.handleCall();
    }
    
    public List<CallStatus> getOngoingCalls() {
        return callCenter.getOngoingCalls();
    }

    public void endCall(UUID callUuid) {
        callCenter.handleGoodBye(callUuid);
    }
    
    public void escalate(UUID callUuid) {
        callCenter.escalate(callUuid);
    }
}
