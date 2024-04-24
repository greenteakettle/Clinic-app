package com.example.clinic.auth;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clinic.R;
import com.example.clinic.home.HomeActivity;
import com.example.clinic.model.Medcard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class RegisterPatientActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mBloodGroup;
    private EditText mContactNumber;
    private EditText mAddress;
    private EditText mEmail;
    private EditText mPassword;
    private Button mRegister;
    private Button mDoctorRegister;
    private EditText mDateOfBirth; // New EditText for Date of Birth

    // RadioGroup & RadioButton
    private RadioGroup mGender;

    //Firebase Auth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Database Reference
    private DatabaseReference mUserDetails = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mMedcardRef = FirebaseDatabase.getInstance().getReference().child("Medcards");

    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);

        // Initialize progress dialog
        mRegProgress = new ProgressDialog(this);

        // Toolbar
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Регистрация пациента");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        mDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        //Other fields initialization
        mName = findViewById(R.id.reg_name_edit_text);
        mBloodGroup = findViewById(R.id.reg_bloodgroup_edit_text);
        mContactNumber = findViewById(R.id.reg_contact_edit_text);
        mAddress = findViewById(R.id.reg_address_edit_text);
        mEmail = findViewById(R.id.reg_email_edit_text);
        mPassword = findViewById(R.id.reg_password_edit_text);
        mRegister = findViewById(R.id.reg_button);
        mDoctorRegister = findViewById(R.id.doctor_register_button);

        // Set click listener for doctor registration button
        mDoctorRegister.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterPatientActivity.this, RegisterDoctorActivity.class);
            startActivity(intent);
        });

        // Set click listener for register button
        mRegister.setOnClickListener(v -> {
            // Retrieve user inputs
            String name = mName.getText().toString();
            String bloodgroup = mBloodGroup.getText().toString();
            String contactnumber = mContactNumber.getText().toString();
            String address = mAddress.getText().toString();
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            String gender;

            //RadioGroup
            mGender = findViewById(R.id.reg_gender_radiogroup);
            int checkedId = mGender.getCheckedRadioButtonId();

            if(checkedId == R.id.reg_male_radiobtn){
                gender = "Мужской";
            }
            else if(checkedId == R.id.reg_female_radiobtn){
                gender = "Женский";
            }
            else {
                gender = "";
                Toast.makeText(getBaseContext(),"Выберите пол",Toast.LENGTH_LONG).show();
                return;
            }

            String dateOfBirth = mDateOfBirth.getText().toString(); // Получаем выбранную дату рождения

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(contactnumber) || TextUtils.isEmpty(address)){
                Toast.makeText(RegisterPatientActivity.this,"Пожалуйста заполните все поля",Toast.LENGTH_LONG).show();
                return;
            }

            // Show progress dialog
            mRegProgress.setTitle("Создание аккаунта");
            mRegProgress.setMessage("Пожалуйста подождите!");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.show();

            // Create account with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(RegisterPatientActivity.this, task -> {
                        if(task.isSuccessful()){
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();

                            // Add user type to database
                            mUserDetails.child("User_Type").child(uid).child("Type").setValue("Patient");

                            // Create user details hashmap
                            HashMap<String,String> userDetails = new HashMap<>();
                            userDetails.put("Name",name);
                            userDetails.put("Gender",gender);
                            userDetails.put("Blood_Group",bloodgroup);
                            userDetails.put("Contact_N0",contactnumber);
                            userDetails.put("Address",address);
                            userDetails.put("Email",email);
                            userDetails.put("DateOfBirth", dateOfBirth); // Добавляем дату рождения

                            // Add user details to database
                            mUserDetails.child("Patient_Details").child(uid).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mRegProgress.dismiss();
                                    Toast.makeText(RegisterPatientActivity.this,"Аккаунт успешно создан",Toast.LENGTH_SHORT).show();
                                    // Generate medcard ID
                                    String cardId = generateMedcardId();
                                    // Create Medcard object
                                    Medcard medcard = new Medcard(cardId, name, bloodgroup, dateOfBirth, gender, "");
                                    // Add Medcard to database
                                    mMedcardRef.child(uid).setValue(medcard).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                verifyEmail(email);
                                            } else {
                                                Toast.makeText(RegisterPatientActivity.this,"Произошла ошибка при создании медкарты",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            mRegProgress.hide();
                            Toast.makeText(RegisterPatientActivity.this,"Произошла ошибка при создании аккаунта",Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void verifyEmail(final String email) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterPatientActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.verify_email, null);
        TextView userEmail = mView.findViewById(R.id.verify_email);
        final TextView sentVerification = mView.findViewById(R.id.verify_email_sent);
        Button verifyEmail = mView.findViewById(R.id.verify_button);
        Button continuebutton = mView.findViewById(R.id.verify_continue);

        userEmail.setText(email);

        verifyEmail.setOnClickListener(view -> mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                sentVerification.setText("Мы отправили письмо на "+email);
            }
            else {
                sentVerification.setText("Не получилось отправить письмо для подтверждения");
            }
        }));

        continuebutton.setOnClickListener(view -> {
            Intent main_Intent = new Intent(RegisterPatientActivity.this, HomeActivity.class);
            startActivity(main_Intent);
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    // Method to generate 5-digit medcard ID
    private String generateMedcardId() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    // Method to show the Date Picker Dialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and set the listener to get the selected date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date in the EditText
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        mDateOfBirth.setText(dateFormat.format(calendar.getTime()));
                    }
                }, year, month, day);

        // Set max date to today to prevent selecting future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Show the DatePickerDialog
        datePickerDialog.show();
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
