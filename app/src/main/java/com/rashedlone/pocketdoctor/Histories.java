package com.rashedlone.pocketdoctor;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Raashidlone on 31-07-2018.
 */

public class Histories extends AppCompatActivity {


    private DatabaseReference myRef;
    private EditText allergy;
    private Button add;
    private HashMap<String, String> map;
    private SharedPreferences sp;
    private ListView list;
    private ProgressBar process;
    private List<String> item = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.medication);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(sp.getString("email","")).child("History");


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


        allergy = findViewById(R.id.allergy);
        add = findViewById(R.id.add);
        process = findViewById(R.id.process);
        list = findViewById(R.id.allergy_items);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(allergy.getText().toString().isEmpty())
                    allergy.setError("Field cannot be empty!");
                else
                    addAllergy(allergy.getText().toString());

            }
        });

        map = new HashMap<>();
        getData();


    }



    private void getData() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();
                    String value = snapshot.getValue(String.class);

                    item.add(key);
                    map.put(key, value);

                }

                initList();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });



    }

    private void initList() {

        process.setVisibility(View.GONE);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, item );
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value = arrayAdapter.getItem(position);
                showFullInfo(value);
            }
        });
    }

    public void showFullInfo(final String val)
    {

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(val);

        ab.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {


            }
        });
        ab.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {


                delete(val);


            }
        });

        ab.show();

    }

    private void delete(String s) {


        myRef.child(s).removeValue();
        Toast.makeText(getApplicationContext(), "Removed!", Toast.LENGTH_SHORT).show();
        refresh();


    }


    private void addAllergy(String s) {


        myRef.child(s).setValue(s);
        Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
        refresh();

    }

    private void refresh() {

        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);

    }

}