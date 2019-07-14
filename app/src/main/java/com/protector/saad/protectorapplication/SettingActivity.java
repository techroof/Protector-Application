package com.protector.saad.protectorapplication;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.protector.saad.protectorapplication.Contact2Activity;
import com.protector.saad.protectorapplication.Contact3Activity;
import com.protector.saad.protectorapplication.Contact4Activity;
import com.protector.saad.protectorapplication.Contact5Activity;
import com.protector.saad.protectorapplication.ContactActivity;
import com.protector.saad.protectorapplication.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.util.HashMap;

public class SettingActivity extends AppCompatActivity  {
    public EditText num1, num2, num3, num4, num5,nameEt,emailEt;
    private DatabaseReference mRootref,dbref;
    private Button settingsBtn,button;
    private FirebaseAuth mAuth;
    private ImageButton btn1, btn2, btn3, btn4, btn5;
    private static final int RESULT_PICK_CONTACT = 85500;

    static final int SEND_SMS_PERMISSION_REQUEST_CODE=111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        dbref=FirebaseDatabase.getInstance().getReference();

        num1 = (EditText) findViewById(R.id.num1);
        num2 = (EditText) findViewById(R.id.num2);
        num3 = (EditText) findViewById(R.id.num3);
        num4 = (EditText) findViewById(R.id.num4);
        num5 = (EditText) findViewById(R.id.num5);
        nameEt=(EditText) findViewById(R.id.settingNameET);
        emailEt=(EditText)findViewById(R.id.settingEmailET);

        btn1 = (ImageButton) findViewById(R.id.imageButton1);
        btn2 = (ImageButton) findViewById(R.id.imageButton2);
        btn3 = (ImageButton) findViewById(R.id.imageButton3);
        btn4 = (ImageButton) findViewById(R.id.imageButton4);
        btn5 = (ImageButton) findViewById(R.id.imageButton5);

        mRootref = FirebaseDatabase.getInstance().getReference().child("users");
        mRootref.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        settingsBtn = (Button) findViewById(R.id.settingButton);

        mRootref.keepSynced(true);



        num1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact=new Intent(SettingActivity.this,ContactActivity.class);
                startActivity(contact);

            }
        });
        num2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact=new Intent(SettingActivity.this,Contact2Activity.class);
                startActivity(contact);

            }
        });
        num3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact=new Intent(SettingActivity.this,Contact3Activity.class);
                startActivity(contact);

            }
        });
        num4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact=new Intent(SettingActivity.this,Contact4Activity.class);
                startActivity(contact);

            }
        });
        num5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact=new Intent(SettingActivity.this,Contact5Activity.class);
                startActivity(contact);

            }
        });



        mRootref.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mRootref.keepSynced(true);
                        String numb1=dataSnapshot.child("numbers").child("hotline").getValue().toString();
                        String numb2=dataSnapshot.child("numbers").child("Friends and family 1").getValue().toString();
                        String numb3=dataSnapshot.child("numbers").child("Friends and family 2").getValue().toString();
                        String numb4=dataSnapshot.child("numbers").child("Friends and family 3").getValue().toString();
                        String numb5=dataSnapshot.child("numbers").child("Friends and family 4").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        String email=dataSnapshot.child("email").getValue().toString();
                        mRootref.keepSynced(true);

                        if (!numb1.equals("")){
                            btn1.setVisibility(View.VISIBLE);
                        }
                        if (!numb2.equals("")){
                            btn2.setVisibility(View.VISIBLE);
                        }
                        if (!numb3.equals("")){
                            btn3.setVisibility(View.VISIBLE);
                        }
                        if (!numb4.equals("")){
                            btn4.setVisibility(View.VISIBLE);
                        }
                        if (!numb5.equals("")){
                            btn5.setVisibility(View.VISIBLE);
                        }


                        num1.setText(numb1);
                        num2.setText(numb2);
                        num3.setText(numb3);
                        num4.setText(numb4);
                        num5.setText(numb5);
                        emailEt.setText(email);
                        nameEt.setText(name);
                        mRootref.keepSynced(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String number1 = num1.getText().toString();
                String number2 = num2.getText().toString();
                String number3 = num3.getText().toString();
                String number4 = num4.getText().toString();
                String number5 = num5.getText().toString();
                String name=nameEt.getText().toString();
                mRootref.keepSynced(true);

                if (TextUtils.isEmpty(number1)){
                    num1.setError("Field can not be blank");
                }
                if (TextUtils.isEmpty(number2)){
                    num2.setError("Field can not be blank");
                }
                if (TextUtils.isEmpty(number3)){
                    num3.setError("Field can not be blank");
                }
                if (TextUtils.isEmpty(number4)){
                    num4.setError("Field can not be blank");
                }
                if (TextUtils.isEmpty(number5)){
                    num5.setError("Field can not be blank");
                }

                mRootref.keepSynced(true);
                if (!TextUtils.isEmpty(number1) && !TextUtils.isEmpty(number2) && !TextUtils.isEmpty(number3)
                        && !TextUtils.isEmpty(number4) && !TextUtils.isEmpty(number5) && !TextUtils.isEmpty(name) ) {

                    submitNumber(number1, number2, number3, number4, number5,name);
                }

            }
        });

    }




    private void submitNumber(final String numb1, final String numb2,final String numb3,
                              final String numb4,final String numb5,final  String name) {
        mRootref.keepSynced(true);

        HashMap<String, String> UserMap = new HashMap<>();
        UserMap.put("hotline", numb1);
        UserMap.put("Friends and family 1", numb2);
        UserMap.put("Friends and family 2", numb3);
        UserMap.put("Friends and family 3", numb4);
        UserMap.put("Friends and family 4", numb5);
        mRootref.keepSynced(true);

        mRootref.child(mAuth.getCurrentUser().getUid()).child("numbers")
                .setValue(UserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mRootref.keepSynced(true);
                Toast.makeText(SettingActivity.this, "Your entered data has been saved", Toast.LENGTH_SHORT).show();
                mRootref.child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
                Intent main = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(main);
                mRootref.keepSynced(true);
            }
        });
        mRootref.keepSynced(true);


    }



}




