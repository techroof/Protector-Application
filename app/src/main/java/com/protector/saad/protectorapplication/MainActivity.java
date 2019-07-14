package com.protector.saad.protectorapplication;


import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.protector.saad.protectorapplication.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mRootref, dbRef;
    private android.support.v7.widget.Toolbar toolbar;
    private LocationManager locationManager;
    private LocationListener listener;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mToggle;
    private EditText nameEt, latET, longET, addEt;
    private ImageView danger, alertbtn, safebtn, mapBtn, hotlineBtn;
    private static int splashOutTime = 4000;
    static final int SEND_SMS_PERMISSION_REQUEST_CODE = 111, CALL_PERMISSION_REQUEST_CODE = 222, LOCATION_PERMISSION_REQUEST_CODE = 333;
    double latitude, longitude;
    private TextView txt;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        hotlineBtn = (ImageView) findViewById(R.id.hotlineBtn);
        latET = (EditText) findViewById(R.id.lat);
        longET = (EditText) findViewById(R.id.longi);
        addEt = (EditText) findViewById(R.id.address);
        nameEt = (EditText) findViewById(R.id.name);
        alertbtn = (ImageView) findViewById(R.id.scaredBtn);
        safebtn = (ImageView) findViewById(R.id.safeBtn);
        mapBtn = (ImageView) findViewById(R.id.mapBtn);
        danger = (ImageView) findViewById(R.id.dangerbtn);
        mRootref = FirebaseDatabase.getInstance().getReference();

        mRootref = FirebaseDatabase.getInstance().getReference();

        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (mAuth.getCurrentUser() != null) {
            dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*---------------------------------------- Permision Check------------------------------------------------------------*/

        if (checkPermission(android.Manifest.permission.SEND_SMS)) {


        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);


        }
        if (checkPermissionCall(Manifest.permission.CALL_PHONE)) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        }



        /*----------------------------------------------- Safe Button onCreate ----------------------------------------------------*/
        safebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SafeButton();
            }

        });
        /*----------------------------------------------- Danger Button onCreate ---------------------------------------------*/
        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DangerButton();
            }
        });
        /*----------------------------------------------- Hotline Button onCreate ---------------------------------------------*/
        hotlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HotlineButton();
            }
        });
        /*----------------------------------------------- Alert Button onCreate ---------------------------------------------*/
        alertbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertButton();

            }
        });
        /*----------------------------------------------- Map Button onCreate ---------------------------------------------*/
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapButton();
            }
        });
        sendLocationMessage();

    }
    /*----------------------------------------------- onCreate  Ends  ----------------------------------------------------*/

    /*----------------------------------------------- Map Button Method ---------------------------------------------*/
    private void MapButton() {
        NearByPlaces();

    }

    /*----------------------------------------------- Alert Button Method ---------------------------------------------*/
    private void AlertButton() {
        final String[] n = new String[5];
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                n[0] = dataSnapshot.child("numbers").child("Friends and family 1").getValue().toString();
                n[1] = dataSnapshot.child("numbers").child("Friends and family 2").getValue().toString();
                n[2] = dataSnapshot.child("numbers").child("Friends and family 3").getValue().toString();
                n[3] = dataSnapshot.child("numbers").child("Friends and family 4").getValue().toString();
                n[4] = dataSnapshot.child("numbers").child("hotline").getValue().toString();
                String lat = dataSnapshot.child("cordinates").child("latitude").getValue().toString();
                String longi = dataSnapshot.child("cordinates").child("longitude").getValue().toString();
                try {
                    String msg = "I am not feeling well. Help me. \nLocation: " + " http://maps.google.com/maps?q=loc:"
                            + lat + "," + longi + "\nPROTECTOR";
                    if (checkPermission(android.Manifest.permission.SEND_SMS)) {
                        for (int i = 0; i <= 4; i++) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(n[i], null, msg, null, null);
                        }
                        Toast.makeText(MainActivity.this, "Messages have been sent", Toast.LENGTH_LONG).show();
                    } else {

                    }
                    if (checkPermissionCall(Manifest.permission.CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + n[4]));
                        startActivity(intent);

                    } else {

                    }

                } catch (Exception e) {

                    Toast.makeText(MainActivity.this,
                            "Please enter data first. Go to settings", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*----------------------------------------------- Hotline Button Method ---------------------------------------------*/
    private void HotlineButton() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String n = dataSnapshot.child("numbers").child("hotline").getValue().toString();
                try {
                    if (checkPermissionCall(Manifest.permission.CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + n));
                        if (ActivityCompat.checkSelfPermission
                                (MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);

                    } else {

                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Please enter data first. Go to Settings.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*----------------------------------------------- Danger Button method  ----------------------------------------------------*/
    private void DangerButton() {
        final String[] n = new String[5];
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                n[0] = dataSnapshot.child("numbers").child("Friends and family 1").getValue().toString();
                n[1] = dataSnapshot.child("numbers").child("Friends and family 2").getValue().toString();
                n[2] = dataSnapshot.child("numbers").child("Friends and family 3").getValue().toString();
                n[3] = dataSnapshot.child("numbers").child("Friends and family 4").getValue().toString();
                n[4] = dataSnapshot.child("numbers").child("hotline").getValue().toString();
                String lat = dataSnapshot.child("cordinates").child("latitude").getValue().toString();
                String longi = dataSnapshot.child("cordinates").child("longitude").getValue().toString();
                try {
                    String msg = "I am in DANGER. Protect me. \nLocation: " + " http://maps.google.com/maps?q=loc:"
                            + lat + "," + longi + "\nPROTECTOR";
                    if (checkPermission(android.Manifest.permission.SEND_SMS)) {
                        for (int i = 0; i <= 4; i++) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(n[i], null, msg, null, null);
                        }
                        Toast.makeText(MainActivity.this, "Messages have been sent", Toast.LENGTH_LONG).show();
                    } else {

                    }
                    if (checkPermissionCall(Manifest.permission.CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + n[4]));
                        startActivity(intent);

                    } else {

                    }

                } catch (Exception e) {

                    Toast.makeText(MainActivity.this,
                            "Please enter data first. Go to settings", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*----------------------------------------------- Safe Button method----------------------------------------------------*/
    private void SafeButton() {
        DatabaseReference newNotificationref = mRootref.child("notifications").child(mAuth.getCurrentUser().getUid()).push();
        String newNotificationId = newNotificationref.getKey();
        mRootref.child("notifications").child(mAuth.getCurrentUser().getUid()).child(newNotificationId).child("type").setValue("sent");


        final String[] n = new String[5];
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                n[0] = dataSnapshot.child("numbers").child("Friends and family 1").getValue().toString();
                n[1] = dataSnapshot.child("numbers").child("Friends and family 2").getValue().toString();
                n[2] = dataSnapshot.child("numbers").child("Friends and family 3").getValue().toString();
                n[3] = dataSnapshot.child("numbers").child("Friends and family 4").getValue().toString();
                n[4] = dataSnapshot.child("numbers").child("hotline").getValue().toString();
                String lat = dataSnapshot.child("cordinates").child("latitude").getValue().toString();
                String longi = dataSnapshot.child("cordinates").child("longitude").getValue().toString();
                try {
                    String msg = "I am safe now. Thanks. \nLocation: " + " http://maps.google.com/maps?q=loc:" + lat + "," + longi + "\nPROTECTOR";
                    if (checkPermission(android.Manifest.permission.SEND_SMS)) {
                        for (int i = 0; i <= 4; i++) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(n[i], null, msg, null, null);
                        }
                        Toast.makeText(MainActivity.this, "Messages have been sent", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    Toast.makeText(MainActivity.this,
                            "Please enter data first. Go to settings", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // final String msg = "I am safe now. Thanks. "+" http://maps.google.com/maps?q=loc:"+ lat + "," +longi;
    }
    /*--------------- On Start---------*/

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            openStartActivity();
        }

    }

    private void openStartActivity() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
    }

    /*------------------------ Menu Inflater -----------------*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.signOut) {

            FirebaseAuth.getInstance().signOut();
            openStartActivity();

        }

        if (item.getItemId() == R.id.account_setting) {

            Intent settingsIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingsIntent);

        }

        if (item.getItemId() == R.id.profile) {
            //mUserRef.child("online").setValue("true");
            Intent settingsIntent = new Intent(MainActivity.this, Profile.class);
            startActivity(settingsIntent);

        }
        if (item.getItemId() == R.id.news_feed) {
            Intent newsIntent = new Intent(MainActivity.this, NewsFeedActivity.class);
            startActivity(newsIntent);

        }
        return true;
    }

    /*--------------------------------------------------- Permision Method-------------------------------------------*/

    private boolean checkPermission(String permision) {
        int checkPermision = ContextCompat.checkSelfPermission(this, permision);

        return checkPermision == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkPermissionCall(String permision) {
        int checkCallPermision = ContextCompat.checkSelfPermission(this, permision);
        return checkCallPermision == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkPermissionLocation(String permision) {
        int checkLocationPermision = ContextCompat.checkSelfPermission(this, permision);
        return checkLocationPermision == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            case CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            case 10:
                break;
            default:
                break;
        }

    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions
                        (new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.INTERNET}
                                , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.


        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission
                (MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 10000000, 0, listener);


    }
    /*-----------------------------Location Method----------------------------------------------*/

    private void sendLocationMessage() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                final String lat = String.valueOf(location.getLatitude());
                final String longi = String.valueOf(location.getLongitude());
                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String area = addresses.get(0).getSubLocality();
                    String province = addresses.get(0).getAdminArea();
                    String street = addresses.get(0).getFeatureName();
                    String city = addresses.get(0).getLocality();
                    String countryName = addresses.get(0).getCountryName();
                    nameEt.setText(area);
                    addEt.setText(street + " , " + area + " , " + city + " , " + province + " , " + countryName);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "name err", Toast.LENGTH_SHORT).show();
                }


                HashMap<String, String> locationMap = new HashMap<>();
                locationMap.put("latitude", lat);
                locationMap.put("longitude", longi);

                dbRef.child("cordinates").setValue(locationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        latET.setText(lat);
                        longET.setText(longi);
                    }
                });
              /*  Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?q=loc:"
                                + latitude + "," +longitude));
                startActivity(intent);
                */
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    /*------------------------------------ Nearby Places Method -------------------------------*/
    private void NearByPlaces() {
        CharSequence options[] = new CharSequence[]{"Update Location", "Hospital", "Fire bridget", "Police station"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Update location / Find Nearby:");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {

                    if (i == 0) {
                        sendLocationMessage();
                    }

                    //Click Event for each item.
                    if (i == 1) {
                        dbRef.child("cordinates")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String lat = dataSnapshot.child("latitude").getValue().toString();
                                        String longi = dataSnapshot.child("longitude").getValue().toString();

                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("https://www.google.com/maps/search/hospital/@" + lat + "," + longi + ",13z"));
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    if (i == 2) {
                        dbRef.child("cordinates")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String lat = dataSnapshot.child("latitude").getValue().toString();
                                        String longi = dataSnapshot.child("longitude").getValue().toString();

                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("https://www.google.com/maps/search/Fire+Brigade/@" + lat + "," + longi + ",13z"));
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                    }
                    if (i == 3) {
                        dbRef.child("cordinates")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String lat = dataSnapshot.child("latitude").getValue().toString();
                                        String longi = dataSnapshot.child("longitude").getValue().toString();

                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("https://www.google.com/maps/search/police+station/@" + lat + "," + longi + ",13z"));
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Please wait. Your location is being updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

}
