package com.rashedlone.pocketdoctor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by Raashidlone on 12-08-2018.
 */

public class AlarmReceiver extends BroadcastReceiver {


    @SuppressLint("WakelockTimeout")
    @Override
    public void onReceive(Context context, Intent intent) {



        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
            wl.release();
        }
    }


        public void setAlarm(Context context, int t)
    {

        Intent i = new Intent(context,AlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context,0,i,0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),(t*1000),p);
        }


    }

    public void cancelAlarm(Context context)
    {

        Intent i = new Intent(context,AlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(context,0,i,0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(p);
        }


    }


}
