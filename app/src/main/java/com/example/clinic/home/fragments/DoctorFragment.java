package com.example.clinic.home.fragments;

import static java.security.AccessController.getContext;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clinic.R;
import com.example.clinic.model.DoctorList;
import com.example.clinic.patient.PatientViewDoctorProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class DoctorFragment extends Fragment {

    private View rootView;
    private RecyclerView mDoctorList;

    private DatabaseReference mDatabase;


    public DoctorFragment() {
        //Required Empty public constructor otherwise app will crash
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_doctor, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mDoctorList = rootView.findViewById(R.id.doctor_recyclerView);
        mDoctorList.setHasFixedSize(true);
        mDoctorList.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        EditText searchTextBox = rootView.findViewById(R.id.doctor_searchtxt);
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


    public void updateView(String searchQuery) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Doctor_Details");

        Query query = mDatabase.orderByChild("Status").equalTo("1");

        FirebaseRecyclerOptions<DoctorList> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<DoctorList>()
                .setQuery(query, DoctorList.class)
                .build();

        FirebaseRecyclerAdapter<DoctorList, DoctorListViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DoctorList, DoctorListViewHolder>(firebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final DoctorListViewHolder holder, int position, @NonNull final DoctorList model) {
                        holder.setName(model.getName());
                        holder.setSpecialization(model.getSpecialization());

                        // Проверяем пол врача и устанавливаем соответствующую картинку
                        if (model.getGender().equalsIgnoreCase("Мужской")) {
                            holder.setImage(R.mipmap.male_doctor);
                        } else if (model.getGender().equalsIgnoreCase("Женский")) {
                            holder.setImage(R.mipmap.female_doctor);
                        }

                        final String uid = getRef(position).getKey();
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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
                                intent.putExtra("Education", education);
                                intent.putExtra("Experience", experience);
                                intent.putExtra("Contact_N0", contact);
                                intent.putExtra("Shift", shift);
                                intent.putExtra("UserId", uid);
                                intent.putExtra("Gender", gender);
                                startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public DoctorListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_doctor_list, parent, false);
                        return new DoctorListViewHolder(view);
                    }
                };

        mDoctorList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



    public static class DoctorListViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView mImageView;

        public DoctorListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImageView = itemView.findViewById(R.id.profile_id_single_user); // Инициализируем ImageView
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.name_id_single_user);
            userName.setText(name);
        }

        public void setSpecialization(String specialization) {
            TextView userName = mView.findViewById(R.id.special_id_single_user);
            userName.setText(specialization);
        }

        // Добавляем метод для установки изображения
        public void setImage(int resource) {
            mImageView.setImageResource(resource);
        }
    }

}
