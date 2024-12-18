package com.example.clinic.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.clinic.feedback.FeedbackActivity;
import com.example.clinic.model.DoctorList;
import com.example.clinic.patient.PatientViewMedcardActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinic.R;
import com.example.clinic.auth.LoginActivity;
import com.example.clinic.doctor.DoctorProfileActivity;
import com.example.clinic.doctor.ShowDoctorAppointmentActivity;
import com.example.clinic.patient.EditUserProfileActivity;
import com.example.clinic.patient.PatientViewBookedAppointmentActivity;
import com.example.clinic.utils.KeyboardUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    private String Type = "", status = "";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;

    //Firebase Auth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference();


    public static void launch(Activity context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Clinic");

        //DrawerLayout and ToggleButton
        mDrawerLayout = findViewById(R.id.main_drawerLayout);
        mToggle = new ActionBarDrawerToggle(HomeActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //NavigationView
        mNavigationView = findViewById(R.id.main_nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        //TabLayout , SectionPagerAdapter & ViewPager
        mViewPager = findViewById(R.id.main_ViewPager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                hideKeyboard();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                hideKeyboard();
            }
        });

        mTabLayout = findViewById(R.id.main_tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Menu menuNav = mNavigationView.getMenu();
        final MenuItem nav_profile = menuNav.findItem(R.id.nav_profile);
        final MenuItem nav_ShowAppointment = menuNav.findItem(R.id.nav_showAppointment);
        final MenuItem nav_BookedAppointment = menuNav.findItem(R.id.nav_bookedAppointment);
        final MenuItem nav_feedback = menuNav.findItem(R.id.nav_feedback);
        final MenuItem nav_EditUserInfo = menuNav.findItem(R.id.nav_edit_user_info);
        final MenuItem nav_Medcard = menuNav.findItem(R.id.nav_medcard);
        MenuItem nav_logOut = menuNav.findItem(R.id.nav_logout);
        MenuItem nav_logIn = menuNav.findItem(R.id.nav_login);

        nav_profile.setVisible(false);
        nav_ShowAppointment.setVisible(false);
        nav_BookedAppointment.setVisible(false);
        nav_logIn.setVisible(false);
        nav_logOut.setVisible(false);
        nav_feedback.setVisible(false);
        nav_EditUserInfo.setVisible(false);
        nav_Medcard.setVisible(false);


        // Check if user is signed in  or not
        if (currentUser == null) {
            nav_logIn.setVisible(true);

            View mView = mNavigationView.getHeaderView(0);
            TextView userName = (TextView) mView.findViewById(R.id.header_userName);
            TextView userEmail = (TextView) mView.findViewById(R.id.header_userEmail);

            userName.setText("Имя пользователя");
            userEmail.setText("Email пользователя");

            Toast.makeText(getBaseContext(), "Вы не вошли в аккаунт", Toast.LENGTH_LONG).show();
        } else {
            nav_logOut.setVisible(true);
            checkType();
        }
    }

    private void checkType() {

        Menu menuNav = mNavigationView.getMenu();
        final MenuItem nav_profile = menuNav.findItem(R.id.nav_profile);
        final MenuItem nav_ShowAppointment = menuNav.findItem(R.id.nav_showAppointment);
        final MenuItem nav_BookedAppointment = menuNav.findItem(R.id.nav_bookedAppointment);
        final MenuItem nav_feedback = menuNav.findItem(R.id.nav_feedback);
        final MenuItem nav_EditUserInfo = menuNav.findItem(R.id.nav_edit_user_info);
        final MenuItem nav_Medcard = menuNav.findItem(R.id.nav_medcard);

        nav_profile.setVisible(false);
        nav_ShowAppointment.setVisible(false);
        nav_BookedAppointment.setVisible(false);
        nav_feedback.setVisible(false);
        nav_EditUserInfo.setVisible(false);
        nav_Medcard.setVisible(false);

        final String uid = mAuth.getUid().toString();
        mUserDatabase.child("User_Type").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Type = (String) dataSnapshot.child("Type").getValue();
                status = (String) dataSnapshot.child("Status").getValue();

                if ("Patient".equals(Type)) {
                    nav_BookedAppointment.setVisible(true);
                    nav_feedback.setVisible(true);
                    nav_EditUserInfo.setVisible(true);
                    nav_Medcard.setVisible(true);

                    mUserDatabase.child("Patient_Details").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("Name").getValue(String.class);
                            String email = dataSnapshot.child("Email").getValue(String.class);
                            String gender = dataSnapshot.child("Gender").getValue(String.class);

                            CircleImageView circleImageView = findViewById(R.id.header_userPic);

                            if ("Мужской".equals(gender)) {
                                circleImageView.setImageResource(R.mipmap.man);
                            } else if ("Женский".equals(gender)) {
                                circleImageView.setImageResource(R.mipmap.woman);
                            }

                            View mView = mNavigationView.getHeaderView(0);
                            TextView userName = mView.findViewById(R.id.header_userName);
                            TextView userEmail = mView.findViewById(R.id.header_userEmail);

                            userName.setText(name);
                            userEmail.setText(email);

                            Toast.makeText(HomeActivity.this, "Вы вошли в аккаунт", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else if ("Doctor".equals(Type)) {
                    mUserDatabase.child("Doctor_Details").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && "1".equals(dataSnapshot.child("Status").getValue(String.class))) {
                                nav_profile.setVisible(true);
                                nav_ShowAppointment.setVisible(true);

                                String name = dataSnapshot.child("Name").getValue(String.class);
                                String email = dataSnapshot.child("Email").getValue(String.class);
                                String gender = dataSnapshot.child("Gender").getValue(String.class);

                                CircleImageView circleImageView = findViewById(R.id.header_userPic);

                                if ("Мужской".equals(gender)) {
                                    circleImageView.setImageResource(R.mipmap.man);
                                } else if ("Женский".equals(gender)) {
                                    circleImageView.setImageResource(R.mipmap.woman);
                                }

                                View mView = mNavigationView.getHeaderView(0);
                                TextView userName = mView.findViewById(R.id.header_userName);
                                TextView userEmail = mView.findViewById(R.id.header_userEmail);

                                userName.setText(name);
                                userEmail.setText(email);

                                Toast.makeText(HomeActivity.this, "Вы вошли в аккаунт", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, "Ваш аккаунт ожидает подтверждения", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            } else {
                    Toast.makeText(HomeActivity.this, "Ваш аккаунт ожидает подтверждения", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    onStart();
                }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_profile) {
            launchScreen(DoctorProfileActivity.class);
        } else if (itemId == R.id.nav_showAppointment) {
            launchScreen(ShowDoctorAppointmentActivity.class);
        } else if (itemId == R.id.nav_bookedAppointment) {
            launchScreen(PatientViewBookedAppointmentActivity.class);
        } else if (itemId == R.id.nav_login) {
            launchScreen(LoginActivity.class);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getBaseContext(), "Вы успешно вышли из аккаунта", Toast.LENGTH_LONG).show();
            onStart();
        } else if (itemId == R.id.nav_edit_user_info) {
            startActivity(new Intent(HomeActivity.this, EditUserProfileActivity.class));
        } else if (itemId == R.id.nav_feedback) {
            startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
        } else if (itemId == R.id.nav_medcard) {
        startActivity(new Intent(HomeActivity.this, PatientViewMedcardActivity.class));
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchScreen(Class<?> activity) {
        hideKeyboard();
        Intent intent = new Intent(HomeActivity.this, activity);
        startActivity(intent);
    }

    private void hideKeyboard() {
        KeyboardUtils.hideKeyboard(HomeActivity.this);
    }
}
