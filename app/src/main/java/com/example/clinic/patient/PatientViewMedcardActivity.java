package com.example.clinic.patient;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clinic.R;
import com.example.clinic.model.Medcard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientViewMedcardActivity extends AppCompatActivity {

    private DatabaseReference medcardRef;

    private Toolbar mToolbar;

    // Добавьте переменные для TextView, в которых будет отображаться информация о медкарте
    private TextView textViewPatientNameData;
    private TextView textViewDOBData;
    private TextView textViewGenderData;
    private TextView textViewMedicalRecordNumberData;
    private TextView textViewAdditionalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_medical_card);

        // Добавляем кнопку "назад", только если есть ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        // Загружаем информацию о медкарте
        loadMedcardData();

        // Toolbar
        mToolbar = findViewById(R.id.medcard_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Медицинская карта");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadMedcardData() {
        // Получаем идентификатор текущего пользователя
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Запрос к таблице "Medcard" для получения информации о медкарте пациента
        medcardRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
