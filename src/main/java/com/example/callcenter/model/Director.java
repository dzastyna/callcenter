package com.example.callcenter.model;

public class Director extends Employee {



    @Override
    public boolean canHandleRequest(CallerRequestLevel level) {
        super.canHandleRequest(level);

        return true;
    }

    @Override
    public CallerRequestLevel getLevel() {
        return CallerRequestLevel.DIRECTOR;
    }
}
