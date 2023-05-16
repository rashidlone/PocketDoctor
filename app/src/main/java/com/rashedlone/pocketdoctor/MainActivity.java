package com.rashedlone.pocketdoctor;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sp;
    private String[] blood = new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-", "unknown"};
    private DatabaseReference myRef;
    private TextView profile_name;
    private TextView profile_bio;
    private DatabaseReference customRef, rootRef,rootRef2,rootRef3,infoo;
    private HashMap<String, String> map;
    private ImageButton profile_photo;
    private RelativeLayout main;
    private Context context = this;
    private DatabaseReference shareRef;
    private String loc;
    private String share_val;
    public static String version;
    private WebView w1,w2,w3;
    private ArrayList<String> emails;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        View adContainer = findViewById(R.id.adMobView);

        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(sp.getString("banner",""));
        ((RelativeLayout)adContainer).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Bundle b = getIntent().getExtras();
        if(b!=null) {
            String test = b.getString("ads");
            if (test != null && test.equals("yes") && !sp.getString("full_email","").isEmpty()) {
               showadd();
                getIntent().removeExtra("ads");
            }

        }


        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference("break");
        infoo = database.getReference("info");
        rootRef2 = database.getReference("version");
        rootRef3 = database.getReference("block");
        shareRef = database.getReference("share");
        myRef = database.getReference(sp.getString("email", "")).child("BloodGroup");
        customRef = database.getReference(sp.getString("email", ""));
        w1 = new WebView(this);
        w1.loadUrl("file:///android_asset/help.html");
        w2 = new WebView(this);
        w2.loadUrl("file:///android_asset/developer.html");
        w3= new WebView(this);
        w3.loadUrl("file:///android_asset/permissions.html");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        PackageManager pm = context.getPackageManager();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        FloatingActionButton fab = findViewById(R.id.fab);
        TextView name = header.findViewById(R.id.name);
        TextView email = header.findViewById(R.id.email);
        name.setText(sp.getString("name", ""));
        email.setText(sp.getString("full_email", ""));

        ImageView call = findViewById(R.id.call);
        ImageView sms = findViewById(R.id.sms);
        main = findViewById(R.id.main);
        TextView personal = findViewById(R.id.personal_info);
        TextView doctor = findViewById(R.id.doctor_info);
        TextView allergies = findViewById(R.id.allergy_info);
        TextView bld = findViewById(R.id.blood_info);
        TextView medic = findViewById(R.id.medication_info);
        TextView history = findViewById(R.id.histories_info);
        profile_name = findViewById(R.id.user_profile_name);
        profile_bio = findViewById(R.id.user_profile_short_bio);
        profile_photo = findViewById(R.id.user_profile_photo);
        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);*/
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.getString("full_email","").isEmpty())
                    Toast.makeText(getApplicationContext(), "Add an email id first!", Toast.LENGTH_SHORT).show();
                else
                launch(Setting.class);
            }
        });


        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch(PersonalDetails.class);
            }
        });

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch(DoctorDetails.class);
            }
        });

        allergies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch(Allergies.class);
            }
        });

        bld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                show("BloodGroup");
            }
        });

        medic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch(Medications.class);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch(Histories.class);
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<String> test = sp.getStringSet("contact_name", null);
                if (test == null) {
                    showEmergency();

                } else
                    callContacts();
            }
        });


        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Set<String> test = sp.getStringSet("contact_name", null);

                if (test == null && sp.getString("sms_body", "").isEmpty()) {
                    Toast.makeText(context, "Add Quick Message first!", Toast.LENGTH_SHORT).show();
                    msgDialaog();

                } else if (test == null) {
                    Toast.makeText(MainActivity.this, "Add Emergency Contacts first!", Toast.LENGTH_SHORT).show();

                } else {

                    List<String> list = new ArrayList<>(test);

                    StringBuilder b = new StringBuilder();

                    for (int i = 0; i < list.size(); i++) {
                        b.append(phone(list.get(i))).append(";");
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.putExtra("address", b.toString());
                    i.putExtra("sms_body", sp.getString("sms_body", "") + "\n" + sp.getString("loc", ""));
                    i.setType("vnd.android-dir/mms-sms");
                    startActivity(i);
                }

            }
        });


        if (sp.getString("full_email", "").isEmpty()) {
            main.setVisibility(View.INVISIBLE);
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No email id found, please check if you have given all the permissions to the app. to work correctly!", Snackbar.LENGTH_LONG);
            snackbar.show();
            checkRunTimePermission();

        } else {
            checkRunTimePermission();

        }


        map = new HashMap<>();
        show("PersonalDetails");

        saveProfileBit(sp.getString("img", ""));


        share();


        try {
            PackageInfo p = pm.getPackageInfo(context.getPackageName(), 0);
            version = p.versionName;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }

        admin();
        infoo();
        update();
        bl();



    }//on create ends

    private void showadd() {

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



    private void showEmergency() {


        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(false);
        ab.setIcon(R.mipmap.ic_launcher);
        ab.setMessage("You need to add some contacts that you can quickly call or sms when there is an emergency!");
        ab.setTitle("Info:");
        ab.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                launchMultiplePhonePicker();
            }
        });
        ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        ab.show();


    }

    private void launchMultiplePhonePicker() {

        new MultiContactPicker.Builder(MainActivity.this) //Activity/fragment context
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .showPickerForResult(1);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] b = baos.toByteArray();
                        String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("img", encoded);
                        editor.apply();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    saveProfile(selectedImage);
                }

                break;

            case 1:

                if (resultCode == RESULT_OK) {
                    List<ContactResult> results = MultiContactPicker.obtainResult(imageReturnedIntent);

                    saveContacts(results);

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "No contacts selected!", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void saveContacts(List<ContactResult> results) {

        Set<String> name = new HashSet<>();

        SharedPreferences.Editor ed = sp.edit();

        for (int i = 0; i < results.size(); i++) {
            name.add(results.get(i).getDisplayName());
        }


        ed.putStringSet("contact_name", name);

        ed.apply();

    }

    private void saveProfile(Uri selectedImage) {


        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImage);
            Drawable yourDrawable = Drawable.createFromStream(inputStream, selectedImage.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                profile_photo.setImageURI(selectedImage);
                profile_photo.setBackground(yourDrawable);
                profile_photo.getBackground().setAlpha(1);


            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void saveProfileBit(String s) {


        byte[] imageAsBytes = Base64.decode(s.getBytes(), Base64.DEFAULT);

        profile_photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        //cover_photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

    }


    private void show(final String s) {

        customRef.child(s).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();
                    String value = "" + snapshot.getValue(String.class);

                    map.put(key, value);
                }

                String name = map.get("First") + " " + map.get("Last");
                if (map.get("First") != null) {

                    String t = name + "'s";
                    profile_name.setText(t);
                    profile_bio.setText(R.string.emer_info);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("name", name);
                    ed.apply();
                }


                if (s.equals("BloodGroup"))
                    if (map.get("Group") == null)
                        bloodGroup();
                    else
                        info("Blood Group : " + map.get("Group"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public String phone(String id) {

        String ph = null;
        @SuppressLint("Recycle") Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (phones != null) {
            while (phones.moveToNext()) {
                if (phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).equals(id)) {
                    ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    break;
                }

            }
        }

        return ph;

    }

    public void info(String msg) {

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(false);
        ab.setIcon(R.mipmap.ic_launcher);
        ab.setMessage(msg);
        ab.setTitle("Info");
        ab.setPositiveButton("Hide", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        ab.show();

    }


    public void getUserEmail() {

        emails = new ArrayList<>();
        try {

            Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
            for (Account account : accounts) {
                emails.add(account.name);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        emailPicker();
    }

    public void getLocation() {

        SingleShotLocationProvider.requestSingleUpdate(context,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {

                        SharedPreferences.Editor ed = sp.edit();
                        loc = "https://maps.google.com/maps?q=" + location.latitude + "," + location.longitude;
                        ed.putString("loc", loc);
                        ed.apply();

                    }
                });
    }

    private void checkRunTimePermission() {

        String[] permissions = {
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS};


        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {

                if (sp.getString("full_email", "").isEmpty())
                getUserEmail();

                getLocation();
                     }
        });

    }

    private void emailPicker() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        CharSequence[] chars = emails.toArray(new CharSequence[emails.size()]);
        adb.setSingleChoiceItems(chars, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface d, int n) {

                saveEmail(emails.get(n));
                getLocation();
                main.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Logged in as : " + sp.getString("full_email", ""), Toast.LENGTH_LONG).show();

                database.getReference().child(sp.getString("email","")).setValue("");
                d.dismiss();
                refresh();
            }

        });
        adb.setNegativeButton("Cancel", null);
        adb.setTitle("Choose Email:");
        adb.show();

    }

    private void saveEmail(String s) {

        SharedPreferences.Editor ed = sp.edit();

        String[] arrayString = s.split("@");

        ed.putString("full_email", s);
        ed.putString("email", arrayString[0]);
        ed.apply();
    }


    public void share() {


        shareRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    share_val = dataSnapshot.getValue(String.class);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void msgDialaog() {

        final SharedPreferences.Editor ed = sp.edit();

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setIcon(R.mipmap.ic_launcher);
        final EditText et = new EditText(this);
        ab.setView(et);
        et.setLines(6);
        if (sp.getString("sms_body", "").isEmpty())
            et.setText(R.string.emergency_msg);
        et.setText(sp.getString("sms_body", ""));
        et.setHint("Type a quick message that will be used when your send sms to your contacts.");
        ab.setTitle("Sms Body:");
        ab.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                if (et.getText().toString().isEmpty())
                    Toast.makeText(context, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
                else {
                    ed.putString("sms_body", et.getText().toString());
                    ed.apply();
                    Toast.makeText(context, "Message saved!", Toast.LENGTH_SHORT).show();

                }


            }
        });

        ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        ab.show();


    }


    private void callContacts() {

        Set<String> fetch = sp.getStringSet("contact_name", null);
        assert fetch != null;
        List<String> list = new ArrayList<>(fetch);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.mipmap.ic_launcher).setTitle(list.size() + " Contact(s) Added!");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item);

        for (int i = 0; i < list.size(); i++) {
            arrayAdapter.add(list.get(i));
        }


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Set<String> f = sp.getStringSet("contact_name", null);
                assert f != null;
                List<String> li = new ArrayList<>(f);


                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + phone(li.get(which))));
                context.startActivity(intent);


            }
        });
        builderSingle.show();

    }


    private void bloodGroup() {


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.mipmap.ic_launcher);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item, blood);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                myRef.child("Group").setValue(blood[which]);
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();

            }
        });
        builderSingle.show();


    }



    public void admin() {

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String val = dataSnapshot.getValue(String.class);

                    if (val != null && val.equals("true")) {
                        finish();
                        Toast.makeText(getApplicationContext(),"Server Maintenance, we'll be back soon!",Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void infoo() {

        infoo.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String val = dataSnapshot.getValue(String.class);

                    if (val != null && val.length()>5) {
                       info(val);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void bl() {

        rootRef3.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String val = dataSnapshot.getValue(String.class);

                    if (!sp.getString("email","").isEmpty() && val != null && val.contains(sp.getString("email",""))) {

                        gotoHell();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gotoHell() {


        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(false);
        ab.setIcon(R.mipmap.ic_launcher);
        ab.setMessage("We have detected a suspicions activity from your account and you have been blocked for now. If you think your account was blocked by mistake, Contact us at following emails. Thanks!\n\nsupport@betadevelopers.in\n\nadmin@betadevelopers.in");
        ab.setTitle("Info:");
        ab.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

               finish();
               arg0.dismiss();
            }
        });

        ab.show();
    }

    public void update() {

        rootRef2.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String val = dataSnapshot.getValue(String.class);

                    if (val != null && (!val.equals(MainActivity.version))) {
                        finish();
                        Toast.makeText(getApplicationContext(),"Please update the app first!",Toast.LENGTH_LONG).show();
                        openPlay();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openPlay() {

        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

        }
       // show(appPackageName);
    }


    private void launch(Class s) {

        Intent i = new Intent(MainActivity.this, s);
        startActivity(i);

    }

    private void refresh() {

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

    }


    @Override
    protected void onRestart() {
        super.onRestart();

        refresh();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

            if (sp.getString("full_email", "").isEmpty())
                Toast.makeText(this, "No data to sync.", Toast.LENGTH_SHORT).show();
            else {
                refresh();
                Toast.makeText(this, "Syncing data...", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sync) {

            if (sp.getString("full_email", "").isEmpty())
                Toast.makeText(this, "No data to sync.", Toast.LENGTH_SHORT).show();
            else {
                refresh();
                Toast.makeText(this, "Syncing data...", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_settings) {

            if(sp.getString("full_email","").isEmpty())
                Toast.makeText(getApplicationContext(), "Add an email id first!", Toast.LENGTH_SHORT).show();
            else
                launch(Setting.class);

        }
        else if (id == R.id.nav_about)
        {

            helpDialog("Help!", w1);

        }else if (id == R.id.nav_developer)
        {

            helpDialog("About Developer!",w2 );

        } else if (id == R.id.privacy)
    {

        helpDialog("Permissions!",w3);

    }        else if (id == R.id.nav_share) {

            if (share_val.trim().isEmpty())
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share_val);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }


        } else if (id == R.id.nav_exit) {

            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void helpDialog(String str, final View w) {


        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(str)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Hide", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        ((ViewGroup) w.getParent()).removeView(w);
                        dialog.dismiss();

                    }
                });

        dialog.setView(w);
        dialog.show();


    }

    public void alarm(View view) {

        if (sp.getString("alarm_time", "").isEmpty()) {
            Toast.makeText(context, "Please set alarm repeat time in settings.", Toast.LENGTH_SHORT).show();
        } else if (sp.getBoolean("alarm", true)) {
            Toast.makeText(context, "Alarm stopped!", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("alarm", false);
            ed.apply();

        } else {
            int t = Integer.parseInt(sp.getString("alarm_time", ""));
            if (t == 0)
                t++;
            info("Emergency sound will be played after every " + t + " minute(s). To stop alarm, click on the bell icon again.");
            startAlarm(t);
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("alarm", true);
            ed.apply();
        }


    }

    private void startAlarm(int t) {

        final MediaPlayer mp;
        mp = MediaPlayer.create(context, R.raw.alarm);

        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                if (sp.getBoolean("alarm", true))
                    mp.start();
            }
        };

        timer.schedule(hourlyTask, 0L, 1000 * t * 60);


    }

}