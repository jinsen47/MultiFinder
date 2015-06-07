package com.jinsen.multifinder.Events;

/**
 * Created by Jinsen on 15/4/28.
 *
 * This is a message used for bluetooth gatt connection state change.
 */
public class StatusMessage {
    public static int TRASH = 5;
    private String address;
    private int state;

    public StatusMessage(String address, int state) {
        this.state = state;
        this.address = address;
    }

    public int getState() {
        return state;
    }

    public String getAddress() {
        return this.address;
    }
}
