package com.rashedlone.pocketdoctor;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Raashid_Lone on 10/1/2017.
 */

public class Setting extends PreferenceActivity {

    private PreferenceScreen ver;
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        ver = (PreferenceScreen)findPreference("ver");
        ver.setSummary(MainActivity.version);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(sp.getString("email", "")).child("BloodGroup");

        int a = (int) ( Math.random() * 20);

        if(a%2==0) {
            Load l = new Load(this);
            l.showAds();
        }else
        {
            FbLoad l = new FbLoad(this);
            l.showAds();

        }

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen ps, Preference pff) {

        String k = pff.getKey();

            if(k.equals("remove_contacts"))
            {

                dialog("Do you want to remove all your emergency contacts and add new ones?",1);

            }else if(k.equals("remove_blood_group"))
            {

                dialog("Do you want to change or re-add your Blood type?",2);

            }



        return false;
    }

    private void dialog(String s, final int i) {

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(false);
        ab.setIcon(R.mipmap.ic_launcher);
        ab.setMessage(s);
        ab.setTitle("Confirm:");
        ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                ed = sp.edit();
                if(i == 1)
                {
                    ed.remove("contact_name");
                    ed.apply();
                    info("Emergency contacts removed, go to main screen, click on Call button and add new contacts.");

                }else if(i == 2)
                {

                    myRef.child("Group").removeValue();
                    info("Blood Group removed, go to main screen and add new or change blood type. ");

                }


            }
        });

        ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        ab.show();


    }




    public void info(String s)
    {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(false);
        ab.setIcon(R.mipmap.ic_launcher);
        ab.setMessage(s);
        ab.setTitle("Success:");
        ab.setPositiveButton("Hide", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {


            }
        });

        ab.show();

    }
}