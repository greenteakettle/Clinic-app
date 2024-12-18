package com.example.clinic.patient;

import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.clinic.R;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientViewDoctorProfileActivity extends AppCompatActivity {

    private TextView mName, mEducation, mSpecialization, mExperience, mContactNo, mShift;
    private String shift;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    private CircleImageView mProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_doctor_profile);

        //Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.patient_doctorProfile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Профиль врача");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = (TextView) findViewById(R.id.patient_doctorProfile_name);
        mSpecialization = (TextView) findViewById(R.id.patient_doctorProfile_specialization);
        mEducation = (TextView) findViewById(R.id.patient_doctorProfile_education);
        mExperience = (TextView) findViewById(R.id.patient_doctorProfile_experience);
        mContactNo = (TextView) findViewById(R.id.patient_doctorProfile_contact);
        mShift = (TextView) findViewById(R.id.patient_doctorProfile_shift);
        mProfileImage = findViewById(R.id.patient_doctorProfile_pic);

        Button mBookAppointmentBtn = (Button) findViewById(R.id.book_appointment_button);
        mBookAppointmentBtn.setOnClickListener(v -> {

            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(PatientViewDoctorProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    String userId = getIntent().getStringExtra("UserId");
                    String date = dayOfMonth + "-" + (month + 1) + "-" + year;

                    Intent intent = new Intent(PatientViewDoctorProfileActivity.this, BookAppointmentActivity.class);
                    intent.putExtra("Date", date);
                    intent.putExtra("DoctorUserId", userId);
                    intent.putExtra("Shift", shift);
                    startActivity(intent);
                }
            }, day, month, year);
            datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + (3 * 60 * 60 * 1000));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (15 * 24 * 60 * 60 * 1000));
            datePickerDialog.show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String name = getIntent().getStringExtra("Name");
        String education = getIntent().getStringExtra("Education");
        String specialization = getIntent().getStringExtra("Specialization");
        String experience = getIntent().getStringExtra("Experience");
        String contact = getIntent().getStringExtra("Contact_N0");
        shift = getIntent().getStringExtra("Shift");

        String gender = getIntent().getStringExtra("Gender");

        mName.setText(name);
        mEducation.setText(education);
        mSpecialization.setText(specialization);
        mExperience.setText(experience);
        mContactNo.setText(contact);
        mShift.setText(shift);

        if ("Мужской".equals(gender)) {
            mProfileImage.setImageResource(R.mipmap.male_doctor);
        } else if ("Женский".equals(gender)) {
            mProfileImage.setImageResource(R.mipmap.female_doctor);
        }
    }

}
