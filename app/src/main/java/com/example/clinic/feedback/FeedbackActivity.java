package com.example.clinic.feedback;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clinic.R;
import com.example.clinic.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedbackActivity extends AppCompatActivity {

    private TextView mName, mEmail;
    private EditText mFeedbackText;
    private Button mSubmitFeedback;

    private String currentUID;

    private Toolbar mToolbar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        currentUID = mAuth.getCurrentUser().getUid().toString();

        mName = (TextView) findViewById(R.id.feedback_name);
        mEmail = (TextView) findViewById(R.id.feedback_email);
        mFeedbackText = (EditText) findViewById(R.id.feedback_text);
        mSubmitFeedback = (Button) findViewById(R.id.feedback_submit_button);

        // Toolbar
        mToolbar = findViewById(R.id.feedback_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Оставить отзыв");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String feedback = mFeedbackText.getText().toString();
                mDatabase.child("Feedback").child(mAuth.getCurrentUser().getUid()).push().child("Feedback").setValue(feedback);

                startActivity(new Intent(FeedbackActivity.this, HomeActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child("User_Type").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String type = dataSnapshot.child("Type").getValue().toString();
                final String[] name = {""};
                final String[] email = {""};

                if(type.equals("Patient")){

                    mDatabase.child("Patient_Details").child(currentUID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name[0] = dataSnapshot.child("Name").getValue().toString();
                            email[0] = dataSnapshot.child("Email").getValue().toString();

                            mName.setText(name[0]);
                            mEmail.setText(email[0]);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else if(type.equals("Doctor")){

                    mDatabase.child("Doctor_Details").child(currentUID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name[0] = dataSnapshot.child("Name").getValue().toString();
                            email[0] = dataSnapshot.child("Email").getValue().toString();

                            mName.setText(name[0]);
                            mEmail.setText(email[0]);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
