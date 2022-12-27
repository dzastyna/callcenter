package com.example.callcenter;

import com.example.callcenter.dto.CallStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CallCenterRestAdapter {
    
    @Autowired
    private CallCenterService callCenterService;

    @GetMapping("/healthcheck")
    public String healthCheck() {
        return "Call center is up and running";
    }

    @PostMapping("/handlecall")
    public UUID handleCall() {
        UUID uuid = callCenterService.handleCall();
        return uuid;
    }

    @PostMapping("/endcall")
    public void endCall(@RequestParam String callUuid) {
        callCenterService.endCall(UUID.fromString(callUuid));
    }
    
    @GetMapping("/ongoingcalls")
    public List<CallStatus> geOngoingCalls() {
        return callCenterService.getOngoingCalls();
    }
    
    @PostMapping("/escalate")
    public void escalate(@RequestParam String callUuid) {
        callCenterService.escalate(UUID.fromString(callUuid));
    }
}
