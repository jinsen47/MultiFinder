package com.jinsen.multifinder;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jinsen on 15/6/1.
 */
public class FileCache {
    private static FileCache INSTANCE;
    private static final String FILE_NAME = "devices.json";
    private Context mContext;
    private File mCache;
    private ObjectMapper objectMapper = new ObjectMapper();

    private List<DeviceBean> mDevices;

    private FileCache(){
        mContext = FinderApp.getApplication();
        mCache = new File(mContext.getFilesDir().getPath()+ File.separator + FILE_NAME);
        if (!mCache.exists()) {
            try {
                mCache.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static FileCache newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileCache();
        }
        return INSTANCE;
    }

    private List<DeviceBean> loadDevices() {
        List<DeviceBean> list = new ArrayList<>();
        try {
            DeviceBean[] array = objectMapper.readValue(mCache, DeviceBean[].class);

            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DeviceBean> getDevices() {
        if (mDevices == null) {
            mDevices = loadDevices();
        }
        return mDevices;
    }

    public void addorUpdateDevice(String title, String address) {
        if (mDevices == null) {
            mDevices = loadDevices();
        }
        if (mDevices.size() == 0) {
            DeviceBean bean = new DeviceBean(title, address, 0);
            mDevices.add(bean);
            return;
        }

        for (int i = 0; i < mDevices.size(); i++) {
            if (mDevices.get(i).getAddress().toLowerCase().equals(address.toLowerCase())) {
                mDevices.get(i).setTitle(title);
                return;
            }
        }
        DeviceBean bean = new DeviceBean(title, address, 0);
        mDevices.add(bean);
        return;
    }


    public void writeToFile() {
        try {
            objectMapper.writeValue(mCache,mDevices);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
