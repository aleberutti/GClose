package com.becafe.gclose;

import android.app.Application;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookSdk;

public class GCloseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

}
