package com.example.clinic.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clinic.R;
import com.example.clinic.home.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterDoctorActivity extends AppCompatActivity {

    private TextInputLayout mName;
    private TextInputLayout mAge;
    private TextInputLayout mEducation;
    private TextInputLayout mExperience;
    private TextInputLayout mContactNumber;
    private TextInputLayout mAddress;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mRegister;

    //RadioGroup & RadioButton
    private RadioGroup mGender;

    //Firebase Auth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Database Reference
    private DatabaseReference mUserDetails = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mSpecializationsRef = FirebaseDatabase.getInstance().getReference().child("Specialization");

    private TextInputLayout mSpecializationInputLayout;
    private AutoCompleteTextView mSpecializationInput;
    private ArrayList<String> mSpecializationList;
    private ArrayAdapter<String> mSpecializationAdapter;

    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctor);

        // Toolbar
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Регистрация врача");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        // AutoCompleteTextView
        mSpecializationInputLayout = findViewById(R.id.reg_specialization_input_layout);
        mSpecializationInput = findViewById(R.id.reg_specialization_input);
        mSpecializationList = new ArrayList<>();
        mSpecializationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mSpecializationList);
        mSpecializationInput.setAdapter(mSpecializationAdapter);

        loadSpecializations();

        //User Details
        mName = findViewById(R.id.reg_doctor_name);
        mAge = findViewById(R.id.reg_doctor_age);
        mEducation = findViewById(R.id.reg_doctor_education);
        mExperience = findViewById(R.id.reg_doctor_experience);
        mContactNumber = findViewById(R.id.reg_doctor_contact);
        mAddress = findViewById(R.id.reg_doctor_address);
        mEmail = findViewById(R.id.reg_doctor_email);
        mPassword = findViewById(R.id.reg_doctor_password);
        mRegister = findViewById(R.id.reg_doctor_button);

        mRegister.setOnClickListener(v -> {
            // Retrieve data from input fields
            String name = mName.getEditText().getText().toString();
            String age = mAge.getEditText().getText().toString();
            String education = mEducation.getEditText().getText().toString();
            String experience = mExperience.getEditText().getText().toString();
            String contactnumber = mContactNumber.getEditText().getText().toString();
            String address = mAddress.getEditText().getText().toString();
            String email = mEmail.getEditText().getText().toString();
            String password = mPassword.getEditText().getText().toString();
            String gender = "";
            String specialization = mSpecializationInput.getText().toString();

            //RadioGroup
            mGender = findViewById(R.id.reg_doctor_gender_radiogroup);
            int checkedId = mGender.getCheckedRadioButtonId();

            if (checkedId == R.id.reg_doctor_male_radiobtn) {
                gender = "Мужской";
            } else if (checkedId == R.id.reg_doctor_female_radiobtn) {
                gender = "Женский";
            } else {
                Toast.makeText(getBaseContext(), "Выберите пол", Toast.LENGTH_LONG).show();
                return;
            }

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(age) && !TextUtils.isEmpty(contactnumber) && !TextUtils.isEmpty(address)){

                mRegProgress.setTitle("Создание аккаунта");
                mRegProgress.setMessage("Пожалуйста подождите!");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                createAccount(name, age, gender, education, experience, specialization, contactnumber, address, email, password);

            } else {
                Toast.makeText(RegisterDoctorActivity.this, "Пожалуйста заполните все поля", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadSpecializations() {
        mSpecializationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSpecializationList.clear();
                for (DataSnapshot specializationSnapshot : dataSnapshot.getChildren()) {
                    String Specialization = specializationSnapshot.getValue(String.class);
                    if (Specialization != null) {
                        mSpecializationList.add(Specialization);
                    }
                }
                mSpecializationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Не удалось загрузить специализации", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createAccount(final String name, final String age, final String experience, final String education, final String specialization, final String gender, final String contactnumber, final String address, final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterDoctorActivity.this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = currentUser.getUid();

                        mUserDetails.child("User_Type").child(uid).child("Type").setValue("Doctor");

                        HashMap<String, String> userDetails = new HashMap<>();
                        userDetails.put("Name", name);
                        userDetails.put("Age", age);
                        userDetails.put("Education", education);
                        userDetails.put("Experience", experience);
                        userDetails.put("Specialization", specialization);
                        userDetails.put("Gender", gender);
                        userDetails.put("Contact_N0", contactnumber);
                        userDetails.put("Address", address);
                        userDetails.put("Email", email);
                        userDetails.put("Status", "0"); // Adding status with value "0"

                        mUserDetails.child("Doctor_Details").child(uid).setValue(userDetails).addOnCompleteListener(task1 -> {
                            mRegProgress.dismiss();
                            Toast.makeText(RegisterDoctorActivity.this, "Аккаунт успешно создан", Toast.LENGTH_SHORT).show();
                            verifyEmail(email);

                            // Check if a new specialization was entered and save it to the database
                            if (!mSpecializationList.contains(specialization)) {
                                mSpecializationsRef.push().setValue(specialization);
                            }
                        });
                    } else {
                        mRegProgress.hide();
                        Toast.makeText(RegisterDoctorActivity.this, "Произошла ошибка при создании аккаунта", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verifyEmail(final String email) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.verify_email, null);

        TextView userEmail = mView.findViewById(R.id.verify_email);
        final TextView sentVerification = mView.findViewById(R.id.verify_email_sent);
        Button verifyEmail = mView.findViewById(R.id.verify_button);
        Button continuebutton = mView.findViewById(R.id.verify_continue);

        userEmail.setText(email);

        verifyEmail.setOnClickListener(view -> mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sentVerification.setText("Мы отправили письмо на " + email);
            } else {
                sentVerification.setText("Не получилось отправить письмо для подтверждения");
            }
        }));

        continuebutton.setOnClickListener(view -> {
            Intent main_Intent = new Intent(this, HomeActivity.class);
            startActivity(main_Intent);
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}