package com.example.clinic.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import java.util.HashMap;

public class RegisterDoctorActivity extends AppCompatActivity {

    private TextInputLayout mName;
    private TextInputLayout mSpecializationInput;
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

        mSpecializationInput = findViewById(R.id.reg_specialization_input);
        mSpecializationInput.setHint("Введите специализацию");

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
            String specialization = mSpecializationInput.getEditText().getText().toString();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAccount(final String name, final String age, final String gender, final String education, final String experience, final String specialization, final String contactnumber, final String address, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterDoctorActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            String doctorId = mUserDetails.child("Doctor_Details").push().getKey();

                            mUserDetails.child("User_Type").child(uid).child("Type").setValue("Doctor");

                            HashMap<String, String> userDetails = new HashMap<>();
                            userDetails.put("Doctor_ID", doctorId);
                            userDetails.put("Name", name);
                            userDetails.put("Age", age);
                            userDetails.put("Education", education);
                            userDetails.put("Experience", experience);
                            userDetails.put("Specialization", specialization);
                            userDetails.put("Gender", gender);
                            userDetails.put("Contact_N0", contactnumber);
                            userDetails.put("Address", address);
                            userDetails.put("Email", email);
                            userDetails.put("Status", "0");
                            userDetails.put("Shift", "");

                            mUserDetails.child("Doctor_Details").child(uid).setValue(userDetails).addOnCompleteListener(task1 -> {
                                mRegProgress.dismiss();
                                Toast.makeText(RegisterDoctorActivity.this, "Аккаунт успешно создан", Toast.LENGTH_SHORT).show();
                                verifyEmail(email);

                                saveSpecialization(specialization, doctorId);

                                // Вход в аккаунт после успешной регистрации
                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(RegisterDoctorActivity.this, signInTask -> {
                                            if (signInTask.isSuccessful()) {
                                                // Вход успешен
                                                Intent intent = new Intent(RegisterDoctorActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish(); // Закрываем текущую активность
                                            } else {
                                                // Вход не удался
                                                Toast.makeText(RegisterDoctorActivity.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            });
                        } else {
                            mRegProgress.hide();
                            Toast.makeText(RegisterDoctorActivity.this, "Произошла ошибка при создании аккаунта: currentUser is null", Toast.LENGTH_LONG).show();
                        }
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

    private void saveSpecialization(final String specialization, final String doctorId) {
        // Проверяем наличие специализации в базе данных
        mSpecializationsRef.child(specialization).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Специализация уже существует в базе данных
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        if (key != null) {
                            // Помещаем данные доктора внутрь существующей специализации
                            HashMap<String, Object> doctorData = new HashMap<>();
                            doctorData.put("Doctor_ID", doctorId);

                            // Сохраняем данные доктора в базе данных Firebase
                            mSpecializationsRef.child(specialization).child(key).setValue(doctorData)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterDoctorActivity.this, "Специализация успешно обновлена", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegisterDoctorActivity.this, "Ошибка при обновлении специализации", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            return; // Выходим из цикла после добавления информации
                        }
                    }
                } else {
                    // Специализация отсутствует в базе данных, создаем новую запись
                    HashMap<String, Object> doctorData = new HashMap<>();
                    doctorData.put("Doctor_ID", doctorId);

                    // Сохраняем данные доктора в базе данных Firebase
                    mSpecializationsRef.child(specialization).push().setValue(doctorData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterDoctorActivity.this, "Специализация успешно сохранена", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterDoctorActivity.this, "Ошибка при сохранении специализации", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegisterDoctorActivity.this, "Ошибка при проверке специализации", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
