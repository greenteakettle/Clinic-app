package com.example.clinic.doctor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clinic.R;
import com.example.clinic.model.Medcard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorViewMedcardActivity extends AppCompatActivity {

    private EditText additionalInfoEditText;
    private ImageView editCardImageView;

    private Toolbar mToolbar;

    // Добавьте переменные для TextView, в которых будет отображаться информация о медкарте
    private TextView textViewPatientNameData;
    private TextView textViewDOBData;
    private TextView textViewGenderData;
    private TextView textViewMedicalRecordNumberData;
    private TextView textViewAdditionalData;

    private DatabaseReference medcardRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_view_medical_card);

        // Получение переданного идентификатора пациента из интента
        String patientID = getIntent().getStringExtra("patientID");

        // Toolbar
        mToolbar = findViewById(R.id.patient_medcard_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Медицинская карта");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Получение ссылок на элементы интерфейса
        additionalInfoEditText = findViewById(R.id.editTextAdditionalInfo);
        editCardImageView = findViewById(R.id.edit_card);

        // Получаем ссылку на базу данных
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Создаем или получаем ссылку на таблицу "Medcards"
        medcardRef = database.getReference("Medcards");

        // Инициализируем TextView из макета
        textViewPatientNameData = findViewById(R.id.textViewPatientNameData);
        textViewDOBData = findViewById(R.id.textViewDOBData);
        textViewGenderData = findViewById(R.id.textViewGenderData);
        textViewMedicalRecordNumberData = findViewById(R.id.textViewMedicalRecordNumberData);
        textViewAdditionalData = findViewById(R.id.textViewAdditionalData);

        // Настройка обработчика нажатия на изображение "edit_card"
        editCardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAdditionalInfo();
            }
        });

        // Загрузка информации о медкарте
        loadMedcardData(patientID);
    }

    // Метод для обновления дополнительной информации в базе данных Firebase
    private void updateMedCard(String patientID, String newAdditionalInfo) {
        medcardRef.child(patientID).child("information").setValue(newAdditionalInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    showToast("Медкарта обновлена");
                } else {
                    showToast("Ошибка при обновлении медкарты");
                }
            }
        });
    }

    // Метод для открытия поля ввода для редактирования дополнительной информации
    private void editAdditionalInfo() {
        // Скрываем TextView с текущей информацией
        textViewAdditionalData.setVisibility(View.GONE);

        // Отображаем EditText для редактирования информации
        additionalInfoEditText.setVisibility(View.VISIBLE);

        // Отображаем кнопку "Сохранить"
        Button saveButton = findViewById(R.id.update_medcard_button);
        saveButton.setVisibility(View.VISIBLE);

        // Устанавливаем текущее значение в EditText
        additionalInfoEditText.setText(textViewAdditionalData.getText().toString());

        // Настройка обработчика нажатия на кнопку "Сохранить"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newAdditionalInfo = additionalInfoEditText.getText().toString().trim();
                additionalInfoEditText.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                textViewAdditionalData.setText(newAdditionalInfo);
                textViewAdditionalData.setVisibility(View.VISIBLE);

                // Вызываем метод обновления медкарты после сохранения изменений
                String patientID = getIntent().getStringExtra("patientID");
                updateMedCard(patientID, newAdditionalInfo);
            }
        });
    }

    // Метод для отображения уведомления Toast
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadMedcardData(String patientID) {
        // Запрос к таблице "Medcard" для получения информации о медкарте пациента
        medcardRef.child(patientID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Получаем данные о медкарте из таблицы "Medcard"
                    Medcard medcard = dataSnapshot.getValue(Medcard.class);

                    // Устанавливаем значения TextView из данных медкарты
                    if (medcard != null) {
                        textViewPatientNameData.setText(medcard.getPatientName());
                        textViewDOBData.setText(medcard.getDateOfBirth());
                        textViewGenderData.setText(medcard.getGender());
                        textViewMedicalRecordNumberData.setText(medcard.getCardId());
                        textViewAdditionalData.setText(medcard.getInformation());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при чтении данных из базы данных
            }
        });
    }


    // Обработчик нажатия на элементы тулбара
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed(); // Завершаем текущую активность при нажатии на кнопку "назад" на тулбаре
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
