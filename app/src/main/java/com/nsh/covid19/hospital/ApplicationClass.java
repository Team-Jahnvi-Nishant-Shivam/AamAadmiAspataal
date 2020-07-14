package com.nsh.covid19.hospital;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Map config = new HashMap();
        config.put("cloud_name", "thisisnsh");
        config.put("api_key", "");
        config.put("api_secret", "");
        MediaManager.init(this, config);
    }
}
