package com.rashedlone.pocketdoctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by Raashidlone on 13-01-2019.
 */

public class Privacy extends AppCompatActivity{

    private WebView web;
    private SharedPreferences sp;

    @Override protected void onCreate(Bundle b)
{
    super.onCreate(b);
    setContentView(R.layout.privacy);

    sp = PreferenceManager.getDefaultSharedPreferences(this);


    web = findViewById(R.id.web);
    web.loadUrl("file:///android_asset/permissions.html");

    if(sp.getString("acc","").equals("yes")) {

        Intent mainIntent = new Intent(Privacy.this, StartActivity.class);
        startActivity(mainIntent);
        finish();

    }
    }

public void accept(View v){

    SharedPreferences.Editor ed = sp.edit();

    ed.putString("acc", "yes");
    ed.apply();

    Intent i = new Intent(Privacy.this,StartActivity.class);
    startActivity(i);
    finish();


}

public void decline(View v)
{

    finish();
}

}