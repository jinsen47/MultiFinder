package com.jinsen.multifinder.Events;

/**
 * Created by Jinsen on 15/6/2.
 */
public class RssiMessage {
    private String address;
    private int rssi;

    public RssiMessage(String address, int rssi) {
        this.address = address;
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
