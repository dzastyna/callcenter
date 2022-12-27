package com.example.callcenter.model;

import com.example.callcenter.dto.CallStatus;
import com.example.callcenter.dto.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

public class CallCenter {
    
    private Map<UUID, Employee> respondents = new HashMap<>();
    
    private Map<UUID, Employee> managers = new HashMap<>();
    
    private Employee director = new Director();
    
    private Queue<Call> incomingCalls = new LinkedList<>();
    private Queue<Call> waitingForDirector = new LinkedList<>();
    
    
    public CallCenter(int numberOfRespondents, int numberOfManagers) {
        for(int i = 0; i < numberOfRespondents; i++) {
            respondents.put(UUID.randomUUID(), new Respondent());
        }

        for(int i = 0; i < numberOfManagers; i++) {
            managers.put(UUID.randomUUID(), new Manager());
        }
    }
    
    public List<CallStatus> getOngoingCalls() {
        
        List<CallStatus> all = new ArrayList<>();
        
        List<CallStatus> inQueue = incomingCalls
            .stream()
            .map(entry -> new CallStatus(entry.getUuid(), Status.IN_QUEUE))
            .toList();

        List<CallStatus> inDirectorQueue = waitingForDirector
            .stream()
            .map(entry -> new CallStatus(entry.getUuid(), Status.IN_DIRECTOR_QUEUE))
            .toList();
        
        all.addAll(inQueue);
        all.addAll(inDirectorQueue);
        
        all.addAll(
            respondents.values().stream().map(e -> new CallStatus(e.getActiveCallUuid(), Status.RESPONDENT)).toList()
        );

        all.addAll(
            managers.values().stream().map(e -> new CallStatus(e.getActiveCallUuid(), Status.MANAGER)).toList()
        );
        
        all.add(new CallStatus(director.getActiveCallUuid(), Status.DIRECTOR));
        return all;
    }
    
    public UUID handleCall() {
        
        Call call = new Call(UUID.randomUUID());
        if (!assignToAvailableRespondent(call)) {
            incomingCalls.add(call);
        }
        
        return call.getUuid();
    }

    public void handleGoodBye(UUID callId) {
        if (director.isInCall(callId)) {
            director.endCall();
            handleDirectorCallWaitingInQueue();
            return;
        }

        Optional<Call> call = findInIncomingCall(callId);
        if (call.isPresent()) {
            incomingCalls.remove(call.get());
            return;
        }
        
        call = findInCallsWaintingForDirector(callId);
        if (call.isPresent()) {
            waitingForDirector.remove(call.get());
            return;
        }

        Optional<Employee> respondent = findEmployeeHandlingCall(respondents, callId);

        if (respondent.isPresent()) {
            respondent.get().endCall();
            handleRespondentCallWaitingInQueue();
        } else {
            Optional<Employee> manager = findEmployeeHandlingCall(managers, callId);
            if (manager.isPresent()) {
                manager.get().endCall();
            } else {
                throw new IllegalStateException("Cannot find call, that should be terminated.");
            }
        }
        

    }

    private void handleRespondentCallWaitingInQueue() {
        //TODO implement moving call to available respondent, after he has finished his previous call 
    }

    private void handleDirectorCallWaitingInQueue() {
        //TODO implement moving call to the director, after he has finished his previous call
    }

    private Optional<Call> findInIncomingCall(UUID callId) {
        Iterator<Call> it = incomingCalls.iterator();
        while(it.hasNext()) {
            Call call = it.next();
            if (call.getUuid().equals(callId))
                return Optional.of(call);
        }
        return Optional.empty();
    }

    private Optional<Call> findInCallsWaintingForDirector(UUID callId) {
        Iterator<Call> it = waitingForDirector.iterator();
        while(it.hasNext()) {
            Call call = it.next();
            if (call.getUuid().equals(callId))
                return Optional.of(call);
        }
        return Optional.empty();
    }

    private Optional<Employee> findEmployeeHandlingCall(Map<UUID, Employee> employee, UUID callId) {
        return employee
            .values()
            .stream()
            .filter(e -> e.isInCall(callId))
            .findAny();
    }

    public void escalate(UUID callId) {
        if (callWaitsInQueue(callId)) {
            throw new IllegalStateException("Cannot handle request, when call is not in progress");
        }

        Optional<Employee> employee = findEmployeeInTheCall(callId);
        
        if (employee.isPresent()) {
            handleEscalation(employee.get());
        } else {
            throw new IllegalStateException("Unknown call - cannot handle request");
        }
    }

    private Optional<Employee> findEmployeeInTheCall(UUID callId) {
        if (director.isInCall(callId))
            return Optional.of(director);

        Optional<Employee> respondent = this.respondents
            .values()
            .stream()
            .filter(e -> e.isInCall(callId))
            .findAny();
        
        if (respondent.isPresent()) {
            return respondent;
        }

        Optional<Employee> manager = this.managers
            .values()
            .stream()
            .filter(e -> e.isInCall(callId))
            .findAny();
        
        return manager;
    }

    private boolean callWaitsInQueue(UUID callId) {
        return findInIncomingCall(callId).isPresent();
    }

    private boolean assignToAvailableRespondent(Call call) {
        
        Optional<Employee> freeRespondent = 
            respondents.values().stream()
            .filter(e -> e.canHandleCall())
            .findAny();
        
        if (freeRespondent.isPresent()) {
            freeRespondent.get().handleCall(call);
            return true;
        }
        
        return false;
    }

    public void handleEscalation(Employee currentHandler) {
        
        if (currentHandler == null) {
            throw new IllegalArgumentException(
                "Employee cannot be empty");
        }
        
        if (currentHandler == director) {
            throw new IllegalStateException(
                "Director cannot escalate call");
            
        }
        
        Optional<Employee> newHandler = Optional.empty();
        
        if (currentHandler instanceof Respondent) {
            newHandler = escalateToManager(currentHandler);
        }
        
        if (newHandler.isEmpty()) {
            newHandler = escalateToDirector(currentHandler);
        }
        
        if (newHandler.isEmpty()) {
            escalateToDirectorQueue(currentHandler);
        } else {
            currentHandler.passCall(newHandler.get());
        }
        
    }

    private Optional<Employee> escalateToManager(Employee currentHandler) {
        return managers.values().stream().filter(e -> e.isFree()).findAny();
    }

    private Optional<Employee> escalateToDirector(Employee currentHandler) {
        return director.isFree() ? Optional.of(director) : Optional.empty();
    }

    private void escalateToDirectorQueue(Employee currentHandler) {
        currentHandler.passCall(waitingForDirector);
    }
}
