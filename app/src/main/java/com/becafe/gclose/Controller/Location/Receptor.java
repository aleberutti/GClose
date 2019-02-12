package com.becafe.gclose.Controller.Location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.becafe.gclose.View.NavigationActivity;

import java.util.Calendar;

public class Receptor extends BroadcastReceiver {

    private NavigationActivity nav;

    public Receptor(){

    }

    public Receptor(NavigationActivity n){
        nav = n;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context, NavigationActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.putExtra("choosePlaces", false);
        context.startActivity(in);
    }

    public void setAlarm(Context context){
        Log.d("ZAFCarbon","Alrm SET !!");

        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        // add 30 seconds to the calendar object
        // --------------------------CAMBIARA ACA LA CANTIDAD DE SEGUNDOS / CAMBIAR A MINUTOS LA CONSTANTE DE CALENDAR Y PASARLE 5
        cal.add(Calendar.SECOND, 5);
        Intent intent = new Intent(context, Receptor.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the AlarmManager service
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
    }
}
