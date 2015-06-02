package com.jinsen.multifinder;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jinsen on 15/6/1.
 */
public class FinderApp extends Application {
    public static FinderApp mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getApplication() {
        return mContext;
    }
}
