package com.example.clinic.home;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.clinic.home.fragments.DateFragment;
import com.example.clinic.home.fragments.DoctorFragment;
import com.example.clinic.home.fragments.SpecializationFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter{
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                SpecializationFragment specializationFragment = new SpecializationFragment();
                return specializationFragment;

            case 1:
                DoctorFragment doctorFragment = new DoctorFragment();
                return doctorFragment;

            case 2:
                DateFragment dateFragment = new DateFragment();
                return dateFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Специализация";
            case 1:
                return "Врач";
            case 2:
                return "Дата";

            default:
                return null;
        }
    }
}
