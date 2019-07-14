package com.protector.saad.protectorapplication;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewsFeedActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsFeedRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<NewsFeed> list;
    private Toolbar toolbar;
    private DatabaseReference dbref;
    private FirebaseAuth mAuth;
    private EditText postText;
    private String text,time,currentDate,currentTime;
    private Button postBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        toolbar=findViewById(R.id.news_feed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("News Feed");

        mAuth=FirebaseAuth.getInstance();
        dbref= FirebaseDatabase.getInstance().getReference();
        postText=findViewById(R.id.newsfeed_et);
        postBtn=findViewById(R.id.post_btn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        recyclerView=findViewById(R.id.posts_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list=new ArrayList<NewsFeed>();



        dbref.child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                               for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                   NewsFeed newsFeed = dataSnapshot1.getValue(NewsFeed.class);
                                   list.add(newsFeed);
                               }
                               adapter = new NewsFeedRecyclerAdapter(NewsFeedActivity.this, list);
                               recyclerView.setAdapter(adapter);
                           }

                        @Override
                        public void onCancelled (DatabaseError databaseError){

                        }
                });


        ////////////-------- Post Btn ---------////////
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=postText.getText().toString();
                if (!TextUtils.isEmpty(text)){
                    post(text);
                    postText.setHint("What's in your mind...");
                }
            }
        });
    }

    private void post(String text) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd,yyyy");
        currentDate= date.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        currentTime = time.format(calendar.getTime());

        HashMap<String,Object> post=new HashMap<>();
        post.put("text",text);
        post.put("time",currentTime+", "+currentDate);
        dbref.child("posts").push().updateChildren(post);
    }
}
