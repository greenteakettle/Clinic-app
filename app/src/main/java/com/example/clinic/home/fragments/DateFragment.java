package com.example.clinic.home.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinic.R;
import com.example.clinic.model.DoctorList;
import com.example.clinic.patient.PatientViewDoctorProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class DateFragment extends Fragment {

    private TextView mSelectDate, mSelectedDate, mAvailableDate;
    private RecyclerView recyclerView;

    private int count = 0;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    private String currentUserID = "";

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public DateFragment() {
        //Required Empty public constructor otherwise app will crash
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_date, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.date_doctorList_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));


        mSelectDate = (TextView) rootView.findViewById(R.id.date_select_date);
        mSelectedDate = (TextView) rootView.findViewById(R.id.date_selected_date);
        mAvailableDate = (TextView) rootView.findViewById(R.id.date_availableDate);
        mSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String date = dayOfMonth + "-" + (month + 1) + "-" + year;
                        Toast.makeText(rootView.getContext(), date, Toast.LENGTH_SHORT).show();
                        mSelectedDate.setVisibility(View.VISIBLE);
                        mSelectDate.setText(date);
                        mAvailableDate.setVisibility(View.VISIBLE);

                        showDoctorList(date);

                    }
                }, day, month, year);
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + (3 * 60 * 60 * 1000));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (15 * 24 * 60 * 60 * 1000));
                datePickerDialog.show();
            }
        });

        return rootView;
    }

    private void showDoctorList(final String date) {

        Query countQuery = mDatabase.child("Appointment");
        countQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String doctorID = dataSnapshot.getKey().toString();
                mDatabase.child("Appointment").child(doctorID).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        count = (int) dataSnapshot.getChildrenCount();
                        checkAvailabilityOfDoctor(count, doctorID);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void checkAvailabilityOfDoctor(final int count, final String doctorID) {
        Query query = mDatabase.child("Doctor_Details").orderByChild("Name");

        FirebaseRecyclerOptions<DoctorList> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<DoctorList>()
                .setQuery(query, DoctorList.class)
                .build();

        FirebaseRecyclerAdapter<DoctorList, DoctorListVH> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DoctorList, DoctorListVH>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final DoctorListVH holder, int position, @NonNull final DoctorList model) {

                        holder.setDoctorName(model.getName());
                        holder.setSpecialization(model.getSpecialization());
                        if(model.getStatus().equals("1")) {
                            holder.mView.setVisibility(View.VISIBLE);
                        } else {
                            holder.mView.setVisibility(View.GONE);
                        }
                        if (model.getGender().equalsIgnoreCase("Мужской")) {
                            holder.setImage(R.mipmap.male_doctor);
                        } else if (model.getGender().equalsIgnoreCase("Женский")) {
                            holder.setImage(R.mipmap.female_doctor);
                        }
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String name = model.getName();
                                String specialization = model.getSpecialization();
                                String contact = model.getContact_N0();
                                String experience = model.getExperience();
                                String education = model.getEducation();
                                String shift = model.getShift();
                                String gender = model.getGender();

                                Intent intent = new Intent(getContext(), PatientViewDoctorProfileActivity.class);
                                intent.putExtra("Name", name);
                                intent.putExtra("Specialization", specialization);
                                intent.putExtra("Contact_N0", contact);
                                intent.putExtra("Experience", experience);
                                intent.putExtra("Education", education);
                                intent.putExtra("Shift", shift);
                                intent.putExtra("UserId", doctorID);
                                intent.putExtra("Gender", gender);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public DoctorListVH onCreateViewHolder(ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_doctor_list, parent, false);

                        return new DoctorListVH(view);
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class DoctorListVH extends RecyclerView.ViewHolder {

        View mView;

        public DoctorListVH(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDoctorName(String doctorName) {
            TextView userName = (TextView) mView.findViewById(R.id.name_id_single_user);
            userName.setText(doctorName);
        }

        public void setSpecialization(String specialization) {
            TextView userName = (TextView) mView.findViewById(R.id.special_id_single_user);
            userName.setText(specialization);
        }
        public void setImage(int resource) {
            ImageView mImageView = (ImageView) mView.findViewById(R.id.profile_id_single_user);
            mImageView.setImageResource(resource);
        }
    }

}
