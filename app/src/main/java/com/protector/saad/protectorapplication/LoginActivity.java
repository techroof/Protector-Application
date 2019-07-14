package com.protector.saad.protectorapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.protector.saad.protectorapplication.MainActivity;
import com.protector.saad.protectorapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference mRoot;
    private FirebaseAuth mAuth;
    private EditText emailET,passwordET;
    private Button loginBtn;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mAuth=FirebaseAuth.getInstance();
        mRoot=FirebaseDatabase.getInstance().getReference();
        emailET=(EditText)findViewById(R.id.loginEmailET);
        passwordET=(EditText)findViewById(R.id.loginPassET);
        loginBtn=(Button)findViewById(R.id.loginBtn);
        pd=new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailET.getText().toString();
                String password=passwordET.getText().toString();
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    pd.setTitle("Logging in");
                    pd.setMessage("Please wait...");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    login_user(email,password);

                }
            }
        });
    }

    private void login_user(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    String currentUSerid=mAuth.getCurrentUser().getUid();
                    mRoot.child("users").child(currentUSerid).child("device_token").setValue(deviceToken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(mainIntent);

                                }
                            });


                }else
                {
                    pd.dismiss();

                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(LoginActivity.this, "Error : " + task_result, Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
