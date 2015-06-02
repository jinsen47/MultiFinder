package com.jinsen.multifinder.Events;

/**
 * Created by Jinsen on 15/4/28.
 */
public class AlarmMessage {
    private String alarm;

    public AlarmMessage(String alarmString) {
        this.alarm = alarmString;
    }

    public String getAlarm() {
        return alarm;
    }

}
