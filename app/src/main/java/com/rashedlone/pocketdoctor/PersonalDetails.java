package com.rashedlone.pocketdoctor;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Raashidlone on 26-07-2018.
 */

public class PersonalDetails extends AppCompatActivity {


    private Calendar myCalendar;
    private Button dob;
    private RadioButton male,female;
    private EditText first,middle,last,ft,in,weight;
    private SharedPreferences sp;
    private DatabaseReference myRef;
    private ProgressBar process;
    private Map<String, String> map;


    @Override protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.personal_details);

        myCalendar = Calendar.getInstance();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sp = PreferenceManager.getDefaultSharedPreferences(this);



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(sp.getString("email","")).child("PersonalDetails");

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
        ft = findViewById(R.id.ft);
        in = findViewById(R.id.in);
        weight = findViewById(R.id.weight);
        dob = findViewById(R.id.dob);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        process = findViewById(R.id.process);





        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                female.setChecked(false);

            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                male.setChecked(false);

            }
        });




        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(PersonalDetails.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        map = new HashMap<>();
        getData(); //if user has saved data, it will retrieve


    }

    private void checkInfo() {

       /* dob;
        private RadioButton male,female;
        private EditText title,first,middle,last,ft,in,weight;*/


       if(first.getText().toString().isEmpty())
        first.setError(getString(R.string.not_empty));
       else if(middle.getText().toString().isEmpty())
           middle.setError(getString(R.string.not_empty));
       else if(last.getText().toString().isEmpty())
           last.setError(getString(R.string.not_empty));
       else if(!male.isChecked() && !female.isChecked())
           Toast.makeText(getApplicationContext(),"Please select gender first!",Toast.LENGTH_SHORT).show();
       else {

           if(sp.getString("email","").isEmpty())
               Toast.makeText(getApplicationContext(),"Please check if your Email id is added first!",Toast.LENGTH_LONG).show();
           else
           saveData();

       }


    }

    private void saveData() {

       add("First",first.getText().toString());
       add("Middle",middle.getText().toString());
       add("Last",last.getText().toString());
       if(male.isChecked())
       add("Gender","male");
       else
           add("Gender","female");
       add("DOB",dob.getText().toString());
       add("Ft",ft.getText().toString());
       add("In",in.getText().toString());
       add("Weight",weight.getText().toString());

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
        if(getValue("Gender").equals("female"))
            female.setChecked(true);
        else
            male.setChecked(true);
        dob.setText(getValue("DOB"));
        ft.setText(getValue("Ft"));
        in.setText(getValue("In"));
        weight.setText(getValue("Weight"));

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

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(sdf.format(myCalendar.getTime()));
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

            checkInfo();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
