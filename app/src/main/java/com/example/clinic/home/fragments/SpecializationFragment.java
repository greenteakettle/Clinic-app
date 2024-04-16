package com.example.clinic.home.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clinic.R;
import com.example.clinic.model.BookedAppointmentList;
import com.example.clinic.model.DoctorList;
import com.example.clinic.patient.PatientViewDoctorProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpecializationFragment extends Fragment {

    private static final String TAG = SpecializationFragment.class.getSimpleName();
    private View rootView;

    private RecyclerView mRecyclerView;

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private AlertDialog mDialog;

    public SpecializationFragment() {
        //Required Empty public constructor otherwise app will crash
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_specialization, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mRecyclerView = rootView.findViewById(R.id.specialization_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        EditText searchTextBox = rootView.findViewById(R.id.special_searchtxt);
        searchTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateView(editable.toString());
            }
        });

        updateView("");
    }

    private void updateView(String searchQuery) {
        Query query = mDatabase.child("Specialization").orderByKey();
        if (!searchQuery.isEmpty()) {
            query = query.startAt(searchQuery).endAt(searchQuery + "\uf8ff");
        }

        FirebaseRecyclerOptions<DoctorList> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<DoctorList>()
                .setQuery(query, DoctorList.class)
                .build();

        FirebaseRecyclerAdapter<DoctorList, SpecializationViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DoctorList, SpecializationViewHolder>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final SpecializationViewHolder holder, final int position, @NonNull final DoctorList model) {

                        final String Special = getRef(position).getKey();
                        holder.setSpecialization(Special);

                        if (position % 2 == 0) {
                            holder.setImage(1);
                        } else {
                            holder.setImage(2);
                        }

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog(Special);
                            }
                        });

                    }

                    @Override
                    public SpecializationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_specialization_list, parent, false);
                        return new SpecializationViewHolder(view);
                    }

                    @Override
                    public void onDataChanged() {
                        super.onDataChanged();
                        Log.d(TAG, "Specialization RecyclerViewItem Count: " + getItemCount());
                    }
                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void alertDialog(String special) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.doctor_list_dialog, null);

        RecyclerView alertRecyclerView = view.findViewById(R.id.doctor_dialog);
        alertRecyclerView.setHasFixedSize(true);
        alertRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        builder.setView(view);

        Query query = mDatabase.child("Doctor_Details").orderByChild("Specialization").equalTo(special);

        FirebaseRecyclerOptions<BookedAppointmentList> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<BookedAppointmentList>()
                .setQuery(query, BookedAppointmentList.class)
                .build();

        FirebaseRecyclerAdapter<BookedAppointmentList, DoctorListViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<BookedAppointmentList, DoctorListViewHolder>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final DoctorListViewHolder holder, int position, @NonNull final BookedAppointmentList model) {

                        final String doctorID = model.getDoctor_ID();

                        mDatabase.child("Doctor_Details").child(doctorID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String doctorName = getDataSnapshot("Name", dataSnapshot);
                                final String specialization = special; // Get specialization from method parameter
                                final String contact = getDataSnapshot("Contact_N0", dataSnapshot);
                                final String experience = getDataSnapshot("Experience", dataSnapshot);
                                final String education = getDataSnapshot("Education", dataSnapshot);
                                final String shift = getDataSnapshot("Shift", dataSnapshot);

                                holder.setDoctorName(doctorName);
                                holder.setSpecialization(specialization);
                                holder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), PatientViewDoctorProfileActivity.class);
                                        intent.putExtra("Name", doctorName);
                                        intent.putExtra("Specialization", specialization);
                                        intent.putExtra("Contact_N0", contact);
                                        intent.putExtra("Experience", experience);
                                        intent.putExtra("Education", education);
                                        intent.putExtra("Shift", shift);
                                        intent.putExtra("UserId", doctorID);
                                        startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled: " + databaseError.toString());
                            }

                            private String getDataSnapshot(String child, DataSnapshot dataSnapshot) {
                                String value = "";
                                if (dataSnapshot.hasChild(child))
                                    value = Objects.requireNonNull(dataSnapshot.child(child).getValue()).toString();
                                return value;
                            }
                        });
                    }

                    @Override
                    public DoctorListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        @SuppressLint("InflateParams") View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_doctor_list, null);
                        return new DoctorListViewHolder(mView);
                    }

                    @Override
                    public void onDataChanged() {
                        super.onDataChanged();
                        Log.d(TAG, "Specialization-DoctorList RecyclerViewItem Count: " + getItemCount());
                    }
                };
        alertRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public static class DoctorListViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public DoctorListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDoctorName(String doctorName) {
            TextView userName = mView.findViewById(R.id.name_id_single_user);
            userName.setText(doctorName);
        }

        public void setSpecialization(String specialization) {
            TextView userName = mView.findViewById(R.id.special_id_single_user);
            userName.setText(specialization);
        }
    }


    public class SpecializationViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public SpecializationViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setSpecialization(String special) {
            TextView userName = mView.findViewById(R.id.special_id_single_user);
            userName.setText(special);
        }

        public void setImage(int i) {

            CircleImageView imageView = mView.findViewById(R.id.profile_id_single_user);
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.patient_image));

        }

    }
}