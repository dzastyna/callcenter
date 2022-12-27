package com.example.callcenter.model;

public class Respondent extends Employee {

    @Override
    public void handleCall(Call call) {
        super.handleCall(call);
    }

    @Override
    public boolean canHandleRequest(CallerRequestLevel level) {
        super.canHandleRequest(level);
        
        return CallerRequestLevel.RESPONDENT == level;
    }

    @Override
    public CallerRequestLevel getLevel() {
        return CallerRequestLevel.RESPONDENT;
    }

}
