package com.jinsen.multifinder;

/**
 * Created by Jinsen on 15/6/1.
 */
public class DeviceBean {
    private String title;
    private String address;
    private int rssi;

    public String getTitle() {
        return title;
    }

    public DeviceBean(String title, String address, int rssi) {
        this.title = title;
        this.address = address;
        this.rssi = rssi;
    }

    public DeviceBean() {}

    public void setTitle(String title) {
        this.title = title;
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
