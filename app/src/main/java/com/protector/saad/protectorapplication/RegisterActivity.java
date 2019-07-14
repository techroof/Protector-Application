package com.protector.saad.protectorapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.protector.saad.protectorapplication.MainActivity;
import com.protector.saad.protectorapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText RegName,RegEmail,RegPassword,phoneEt;
    private TextView text;
    private Button button;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegName=(EditText)findViewById(R.id.Name);
        RegEmail=(EditText)findViewById(R.id.Email);
        RegPassword=(EditText) findViewById(R.id.Password);
        text=(TextView)findViewById(R.id.SignUP);
        button=(Button)findViewById(R.id.button2);
        phoneEt=(EditText)findViewById(R.id.phoneNum);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mAuth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name=RegName.getText().toString();
                String email=RegEmail.getText().toString();
                String password=RegPassword.getText().toString();
                String phone=phoneEt.getText().toString();

                if(!TextUtils.isEmpty(user_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)
                        || !TextUtils.isEmpty(phone) ){

                    pd.setTitle("Registering User");
                    pd.setMessage("Please wait while we create your account !");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    register_user(user_name, email, password,phone);

                }

            }
        });
    }

    private void register_user(final String user_name, final String email, final String password, final String phone) {



        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
                    final String uid=currentUser.getUid();
                    String deviceToken= FirebaseInstanceId.getInstance().getToken();

                    mRootRef=FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    final HashMap<String,String> userMap=new HashMap<>();
                    userMap.put("device_token",deviceToken);
                    userMap.put("name",user_name);
                    userMap.put("id",uid);
                    userMap.put("email",email);
                    userMap.put("password",password);
                    userMap.put("phone",phone);


                    mRootRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                HashMap<String, String> UserMap2 = new HashMap<>();
                                UserMap2.put("hotline","" );
                                UserMap2.put("Friends and family 1", "");
                                UserMap2.put("Friends and family 2", "");
                                UserMap2.put("Friends and family 3", "");
                                UserMap2.put("Friends and family 4", "");
                                mRootRef=FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                        .child("numbers");
                                mRootRef.setValue(UserMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            HashMap<String,String> locationMap=new HashMap<>();
                                            locationMap.put("latitude","");
                                            locationMap.put("longitude","");

                                            mRootRef=FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                                    .child("cordinates");
                                            mRootRef.setValue(locationMap);
                                            pd.dismiss();
                                            Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                            startActivity(mainIntent);
                                        }

                                    }
                                });


                            }else{
                                pd.dismiss();

                                String task_result = task.getException().getMessage().toString();

                                Toast.makeText(RegisterActivity.this, "Error : " + task_result, Toast.LENGTH_LONG).show(); }

                        }
                    });
                }else{
                    pd.dismiss();

                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(RegisterActivity.this, "Error : " + task_result, Toast.LENGTH_LONG).show(); }


            }

        });
    }


}
