package com.rashedlone.pocketdoctor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Raashidlone on 26-07-2018.
 */

public class DoctorDetails extends AppCompatActivity {


    private SharedPreferences sp;
    private DatabaseReference myRef;
    private EditText first,middle,last,street,city,state,zip,country,phone;
    private ProgressBar process;
    private HashMap<String, String> map;

    @Override protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.doctor_details);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(sp.getString("email","")).child("FamilyDoctor");

        View adContainer = findViewById(R.id.adMobView);

        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(sp.getString("banner",""));
        ((RelativeLayout)adContainer).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        int a = (int) ( Math.random() * 20);

        if(a%2==0) {
            Load l = new Load(this);
            l.showAds();
        }else
        {
            FbLoad l = new FbLoad(this);
            l.showAds();

        }

        first = findViewById(R.id.first);
        middle = findViewById(R.id.middle);
        last = findViewById(R.id.last);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        zip = findViewById(R.id.zip);
        country = findViewById(R.id.country);
        phone = findViewById(R.id.phone);
        process = findViewById(R.id.process);



        map = new HashMap<>();
        getData(); //if user has saved data, it will retrieve


    }


    private void saveData() {

        add("First",first.getText().toString());
        add("Middle",middle.getText().toString());
        add("Last",last.getText().toString());
        add("Street",street.getText().toString());
        add("City",city.getText().toString());
        add("State",state.getText().toString());
        add("Zip",zip.getText().toString());
        add("Country",country.getText().toString());
        add("Phone",phone.getText().toString());

        Toast.makeText(getApplicationContext(),"Data Saved!",Toast.LENGTH_SHORT).show();
        finish();

    }

    private void add(String t, String d)
    {
        myRef.child(t).setValue(d);

    }


    private void getData() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();
                    String value = snapshot.getValue(String.class);

                    map.put(key, value);

                }


                loadValues();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    private void loadValues() {


        first.setText(getValue("First"));
        middle.setText(getValue("Middle"));
        last.setText(getValue("Last"));
        street.setText(getValue("Street"));
        city.setText(getValue("City"));
        state.setText(getValue("State"));
        zip.setText(getValue("Zip"));
        country.setText(getValue("Country"));
        phone.setText(getValue("Phone"));

        process.setVisibility(View.GONE);
    }


    public String getValue(String s) {

        String ret;
        if(!map.containsKey(s))
            ret = "";
        else
            ret = map.get(s);

        return ret;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.personal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            saveData();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
