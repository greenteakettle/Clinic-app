package com.example.clinic.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.clinic.R;
import com.example.clinic.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout mEmail;
    private Button mResetPassword;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Сбросить пароль");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mEmail = (TextInputLayout) findViewById(R.id.forgot_password_email);

        mResetPassword = (Button) findViewById(R.id.forgot_password_reset_btn);
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = mEmail.getEditText().getText().toString();

                if (!TextUtils.isEmpty(emailAddress)) {
                    mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Письмо для сброса пароля было отправлено на ваш Email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Пожалуйста введите правильный Email для сброса пароля", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Пожалуйста введите Email, чтобы сбросить пароль", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
