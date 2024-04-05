package com.example.clinic.patient;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinic.R;
import com.example.clinic.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class BookAppointmentActivity extends AppCompatActivity implements View.OnClickListener {

    private String date, time = "", shift;
    private TextView selectDate;
    private Toolbar mToolbar;
    private Button mConfirm;
    private int flagChecked = 0;

    private LinearLayout morningLayout, eveningLayout;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    private CardView[] timeSlots = new CardView[30];
    private DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference().child("Appointment");
    private DatabaseReference patientDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Toolbar
        mToolbar = findViewById(R.id.patient_bookAppointment);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Записаться на прием");

        morningLayout = findViewById(R.id.morning_shift);
        eveningLayout = findViewById(R.id.evening_shift);
        shift = getIntent().getStringExtra("Shift");

        if ("Morning".equals(shift)) {
            morningLayout.setVisibility(View.VISIBLE);
            eveningLayout.setVisibility(View.GONE);
        } else {
            eveningLayout.setVisibility(View.VISIBLE);
            morningLayout.setVisibility(View.GONE);
        }

        mConfirm = findViewById(R.id.confirm_appointment);
        mConfirm.setOnClickListener(v -> {
            if (flagChecked != 0) {
                bookAppointment();
            } else {
                Toast.makeText(BookAppointmentActivity.this, "Пожалуйста выберите время", Toast.LENGTH_SHORT).show();
            }
        });

        selectDate = findViewById(R.id.bookAppointment_selectDate);

        date = getIntent().getStringExtra("Date");
        selectDate.setText(date);
        selectDate.setOnClickListener(v -> showDatePicker());

        initializeTimeSlots();
    }

    private void initializeTimeSlots() {
        for (int i = 0; i < 30; i++) {
            int resId = getResources().getIdentifier("time" + (i + 1), "id", getPackageName());
            timeSlots[i] = findViewById(resId);
            timeSlots[i].setOnClickListener(this);
            timeSlots[i].setTag(i + 1); // Установка тега для каждого слота времени
        }
    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag(); // Получение тега, чтобы определить индекс
        checkIsBooked(index);
    }

    private void checkIsBooked(int index) {
        if (flagChecked != 0) {
            setDefaultColor(flagChecked);
        }
        flagChecked = index;
        setColorGreen(index);
    }

    private void setDefaultColor(int index) {
        timeSlots[index - 1].setCardBackgroundColor(getResources().getColor(R.color.skyBlue));
    }

    private void setColorGreen(int index) {
        timeSlots[index - 1].setCardBackgroundColor(Color.GREEN);
    }

    private void showDatePicker() {
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(BookAppointmentActivity.this, (view, year1, month1, dayOfMonth) -> {
            date = dayOfMonth + "-" + (month1 + 1) + "-" + year1;
            selectDate.setText(date);
            onStart();
        }, year, month, day);
        datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + (3 * 60 * 60 * 1000));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (15 * 24 * 60 * 60 * 1000));
        datePickerDialog.show();
    }

    private void bookAppointment() {
        if (date != null) { // Проверяем, что дата не равна null
            appointmentRef.child(getIntent().getStringExtra("Doctor_ID")).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (int i = 1; i <= 30; i++) {
                        if (dataSnapshot.hasChild(String.valueOf(i))) {
                            if (dataSnapshot.child(String.valueOf(i)).child("PatientID").getValue() != null && dataSnapshot.child(String.valueOf(i)).child("PatientID").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                                Toast.makeText(BookAppointmentActivity.this, "У вас уже есть запись", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    setTime(flagChecked);
                    appointmentRef.child(getIntent().getStringExtra("Doctor_ID")).child(date).child(String.valueOf(flagChecked)).child("PatientID").setValue(mAuth.getCurrentUser().getUid());
                    patientDatabase.child("Doctor_Details").child(getIntent().getStringExtra("Doctor_ID")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String doctorName = dataSnapshot.child("Name").getValue().toString();

                            HashMap<String, String> details = new HashMap<>();
                            details.put("Doctor_ID", getIntent().getStringExtra("Doctor_ID"));
                            details.put("Date", date);
                            details.put("Time", time);

                            patientDatabase.child("Booked_Appointments").child(mAuth.getCurrentUser().getUid()).push().setValue(details);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    startActivity(new Intent(BookAppointmentActivity.this, PatientViewBookedAppointmentActivity.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(this, "Дата не выбрана", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTime(int index) {
        switch (index) {
            case 1:
                time = "08:00";
                break;
            case 2:
                time = "08:20";
                break;
            case 3:
                time = "08:40";
                break;
            case 4:
                time = "09:00";
                break;
            case 5:
                time = "09:20";
                break;
            case 6:
                time = "09:40";
                break;
            case 7:
                time = "10:00";
                break;
            case 8:
                time = "10:20";
                break;
            case 9:
                time = "10:40";
                break;
            case 10:
                time = "11:00";
                break;
            case 11:
                time = "11:20";
                break;
            case 12:
                time = "11:40";
                break;
            case 13:
                time = "14:00";
                break;
            case 14:
                time = "14:20";
                break;
            case 15:
                time = "14:40";
                break;
            case 16:
                time = "15:00";
                break;
            case 17:
                time = "15:20";
                break;
            case 18:
                time = "15:40";
                break;
            case 19:
                time = "16:00";
                break;
            case 20:
                time = "16:20";
                break;
            case 21:
                time = "16:40";
                break;
            case 22:
                time = "17:00";
                break;
            case 23:
                time = "17:20";
                break;
            case 24:
                time = "17:40";
                break;
            case 25:
                time = "18:00";
                break;
            case 26:
                time = "18:20";
                break;
            case 27:
                time = "18:40";
                break;
            case 28:
                time = "21:00";
                break;
            case 29:
                time = "21:20";
                break;
            case 30:
                time = "21:40";
                break;
            default:
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Вы не авторизированы. Войдите в аккаунт, чтобы продолжить", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(BookAppointmentActivity.this, LoginActivity.class));
            finish();
        } else {
            flagChecked = 0;
            if (date != null) {
                appointmentRef.child(getIntent().getStringExtra("Doctor_ID")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (int i = 1; i <= 30; i++) {
                            if (dataSnapshot.child(date).hasChild(String.valueOf(i))) {
                                timeSlots[i - 1].setCardBackgroundColor(Color.RED);
                                timeSlots[i - 1].setClickable(false);
                            } else {
                                timeSlots[i - 1].setCardBackgroundColor(getResources().getColor(R.color.skyBlue));
                                timeSlots[i - 1].setClickable(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Обработка ошибок базы данных
                    }
                });
            } else {
                Toast.makeText(this, "Дата не выбрана", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
