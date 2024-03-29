package com.example.clinic.patient;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clinic.R;
import com.example.clinic.model.BookedAppointmentList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class PatientViewBookedAppointmentActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView recyclerView;

    private String BookedAPKey = "", Appointment_date, slot , Appointment_time , doctorID, currentUID;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_booked_appointment);

        currentUID = mAuth.getCurrentUser().getUid().toString();

        mToolbar = (Toolbar) findViewById(R.id.show_bookedAppointment);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Запланированные визиты");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.show_Appointment_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mDatabase.child("Booked_Appointments").child(currentUID);

        FirebaseRecyclerOptions<BookedAppointmentList> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<BookedAppointmentList>()
                .setQuery(query, BookedAppointmentList.class)
                .build();

        FirebaseRecyclerAdapter<BookedAppointmentList, BookedAppointmentsVH> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<BookedAppointmentList, BookedAppointmentsVH>(firebaseRecyclerOptions){

                    @Override
                    public BookedAppointmentsVH onCreateViewHolder(ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.single_booked_appointment,parent,false);
                        return new BookedAppointmentsVH(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final BookedAppointmentsVH holder, final int position, @NonNull final BookedAppointmentList model) {

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doctorID = model.getDoctor_ID().toString();

                                BookedAPKey = getRef(position).getKey().toString();
//                                Toast.makeText(Patient_ShowBookedAppointmentActivity.this, "Key = "+BookedAPKey, Toast.LENGTH_SHORT).show();

                                Appointment_date = model.getDate();
                                Appointment_time = model.getTime();
                                changeSlotToTime(Appointment_time);
//                                Toast.makeText(Patient_ShowBookedAppointmentActivity.this,"Doctor - "+doctorID, Toast.LENGTH_SHORT).show();

                                alertDialog();

                            }
                        });
                        mDatabase.child("Doctor_Details").child( model.getDoctor_ID().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String doctorName = dataSnapshot.child("Name").getValue(String.class);
                                String specialization = dataSnapshot.child("Specialization").getValue(String.class);

                                holder.setDoctorName(doctorName);
                                holder.setSpecialization(specialization);
                                holder.setDate(model.getDate());
                                holder.setTime(model.getTime());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void alertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientViewBookedAppointmentActivity.this);
        builder.setIcon(R.drawable.question).setTitle("Отменить визит");
        builder.setMessage("Вы уверены, что хотите отменить визит?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                Toast.makeText(Patient_ShowBookedAppointmentActivity.this, Appointment_date, Toast.LENGTH_SHORT).show();
//                Toast.makeText(Patient_ShowBookedAppointmentActivity.this, "KEY = "+BookedAPKey, Toast.LENGTH_SHORT).show();
//                Toast.makeText(Patient_ShowBookedAppointmentActivity.this, "Slot = "+slot, Toast.LENGTH_SHORT).show();

                mDatabase.child("Appointment").child(doctorID).child(Appointment_date).child(slot).removeValue();
                mDatabase.child("Booked_Appointments").child(currentUID).child(BookedAPKey).removeValue();
                onStart();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public class BookedAppointmentsVH extends RecyclerView.ViewHolder{

        View mView;

        public BookedAppointmentsVH(View itemView) {
            super(itemView);

            mView = itemView;
        }


        public void setDoctorName(String doctorName) {
            TextView name = (TextView) mView.findViewById(R.id.single_doctorName);
            name.setText(doctorName);
        }

        public void setSpecialization(String specialization) {
            TextView specializationTV = (TextView) mView.findViewById(R.id.single_doctorSpeciality);
            specializationTV.setText(specialization);
        }


        public void setTime(String time) {

            TextView appointmentTime = (TextView) mView.findViewById(R.id.single_time);
            appointmentTime.setText(time);
        }

        public void setDate(String date) {

            TextView appointmentDate = (TextView) mView.findViewById(R.id.single_date);
            appointmentDate.setText(date);

        }

    }

    private void changeSlotToTime(String appointment_time) {

        switch (appointment_time){

            case "08:00":
                slot = "1";
                break;
            case "08:20":
                slot = "2";
                break;
            case "08:40":
                slot = "3";
                break;
            case "09:00":
                slot = "4";
                break;
            case "09:20":
                slot = "5";
                break;
            case "09:40":
                slot = "6";
                break;
            case "10:00":
                slot = "7";
                break;
            case "10:20":
                slot = "8";
                break;
            case "10:40":
                slot = "9";
                break;
            case "11:00":
                slot = "10";
                break;
            case "11:20":
                slot = "11";
                break;
            case "11:40":
                slot = "12";
                break;
            case "14:00":
                slot = "13";
                break;
            case "14:20":
                slot = "14";
                break;
            case "14:40":
                slot = "15";
                break;
            case "15:00":
                slot = "16";
                break;
            case "15:20":
                slot = "17";
                break;
            case "15:40":
                slot = "18";
                break;
            case "16:00":
                slot = "19";
                break;
            case "16:20":
                slot = "20";
                break;
            case "16:40":
                slot = "21";
                break;
            case "17:00":
                slot = "22";
                break;
            case "17:20":
                slot = "23";
                break;
            case "17:40":
                slot = "24";
                break;
            case "18:00":
                slot = "25";
                break;
            case "18:20":
                slot = "26";
                break;
            case "18:40":
                slot = "27";
                break;
            case "21:00":
                slot = "28";
                break;
            case "21:20":
                slot = "29";
                break;
            case "21:40":
                slot = "30";
                break;

            default:
                break;
        }
    }
}
