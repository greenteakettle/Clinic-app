package com.example.clinic.doctor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.clinic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DoctorProfileActivity extends AppCompatActivity {

    private TextView mName, mEmail, mSpecialization, mExperience, mAge, mContact, mAddress, mEducation;
    private Button mShowRosterPlanButton, mEditProfileButton;
    private Toolbar mToolbar;

    private String name, specialization, experience, education, email, age, contact, address, shift;

    private DatabaseReference mDoctorDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        mName = (TextView) findViewById(R.id.doctor_name);
        mSpecialization = (TextView) findViewById(R.id.doctor_specialization);
        mExperience = (TextView) findViewById(R.id.doctor_experience);
        mEducation = (TextView) findViewById(R.id.doctor_education);
        mEmail = (TextView) findViewById(R.id.doctor_email);
        mAge = (TextView) findViewById(R.id.doctor_age);
        mContact = (TextView) findViewById(R.id.doctor_contact);
        mAddress = (TextView) findViewById(R.id.doctor_address);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.doctor_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Профиль");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = (TextView) findViewById(R.id.doctor_name);

        mShowRosterPlanButton = (Button) findViewById(R.id.show_rosterPlan_button);
        mShowRosterPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBox();
            }
        });

        mEditProfileButton = (Button) findViewById(R.id.edit_profile_button);
        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorProfileActivity.this, EditDoctorProfileActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("Specialization", specialization);
                intent.putExtra("Experience", experience);
                intent.putExtra("Education", education);
                intent.putExtra("Email", email);
                intent.putExtra("Age", age);
                intent.putExtra("Contact_N0", contact);
                intent.putExtra("Address", address);
                startActivity(intent);
            }
        });

    }

    private void alertDialogBox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorProfileActivity.this);

        View view = getLayoutInflater().inflate(R.layout.roster_dialog, null);

        TextView rosterShift = (TextView) view.findViewById(R.id.roster_shift);
        TextView rosterTimingMorning = (TextView) view.findViewById(R.id.roster_time_morning);
        TextView rosterTimingEvening = (TextView) view.findViewById(R.id.roster_time_evening);
        TextView rosterLunchMorning = (TextView) view.findViewById(R.id.roster_break_morning);
        TextView rosterLunchEvening = (TextView) view.findViewById(R.id.roster_break_evening);

        if (Objects.equals(shift, "Утро")) {

            rosterShift.setText(shift);

            rosterTimingMorning.setVisibility(View.VISIBLE);
            rosterTimingEvening.setVisibility(View.GONE);

            rosterLunchMorning.setVisibility(View.VISIBLE);
            rosterLunchEvening.setVisibility(View.GONE);

        } else {

            rosterShift.setText(shift);

            rosterTimingMorning.setVisibility(View.GONE);
            rosterTimingEvening.setVisibility(View.VISIBLE);

            rosterLunchMorning.setVisibility(View.GONE);
            rosterLunchEvening.setVisibility(View.VISIBLE);

        }

        builder.setView(view);
        builder.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDoctorDatabase.child("Doctor_Details").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name = getDataSnapshot("Name", dataSnapshot);
                email = getDataSnapshot("Email", dataSnapshot);
                contact = getDataSnapshot("Contact_N0", dataSnapshot);
                education = getDataSnapshot("Education", dataSnapshot);
                specialization = getDataSnapshot("Specialization", dataSnapshot);
                experience = getDataSnapshot("Experience", dataSnapshot);
                age = getDataSnapshot("Age", dataSnapshot);
                address = getDataSnapshot("Address", dataSnapshot);
                shift = getDataSnapshot("Shift", dataSnapshot);

                mName.setText(name);
                mSpecialization.setText(specialization);
                mExperience.setText(experience);
                mEducation.setText(education);
                mEmail.setText(email);
                mAge.setText(age);
                mContact.setText(contact);
                mAddress.setText(address);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private String getDataSnapshot(String child, DataSnapshot dataSnapshot) {
                String value = "";
                if (dataSnapshot.hasChild(child))
                    value = dataSnapshot.child(child).getValue().toString();
                return value;
            }

        });
    }
}
