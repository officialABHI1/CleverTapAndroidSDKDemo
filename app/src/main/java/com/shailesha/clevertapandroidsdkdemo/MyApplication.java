package com.shailesha.clevertapandroidsdkdemo;

import android.app.Application;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this); // Must be called before super.onCreate()
        super.onCreate();
        // Initialize CleverTap SDK
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG); // Enable for debugging [cite: 2]
    }
}