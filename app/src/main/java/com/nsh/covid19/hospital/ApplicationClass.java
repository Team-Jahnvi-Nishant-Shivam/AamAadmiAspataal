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
        config.put("api_key", "742559798866256");
        config.put("api_secret", "PBlbSiXhmrlCF5-XaLF6dbT4VK8");
        MediaManager.init(this, config);
    }
}
