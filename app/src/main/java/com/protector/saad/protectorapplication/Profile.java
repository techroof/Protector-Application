package com.protector.saad.protectorapplication;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.protector.saad.protectorapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Profile extends AppCompatActivity {
    TextView numberTxt,nameTxt,emailtxt;
    DatabaseReference mRootref;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        numberTxt=(TextView) findViewById(R.id.numberText);
        emailtxt=(TextView)findViewById(R.id.email);
        nameTxt=(TextView)findViewById(R.id.name_tv);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        mAuth=FirebaseAuth.getInstance();
        mRootref=FirebaseDatabase.getInstance().getReference();
        mRootref.child("users").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String num=dataSnapshot.child("phone").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        String email=dataSnapshot.child("email").getValue().toString();
                        numberTxt.setText(num);
                        emailtxt.setText(email);
                        nameTxt.setText(name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
