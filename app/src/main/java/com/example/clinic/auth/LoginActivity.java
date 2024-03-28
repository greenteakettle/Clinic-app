package com.example.clinic.auth;

import android.app.ProgressDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.clinic.R;
import com.example.clinic.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mEmail;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private TextInputLayout mPassword;
    private Button mLogin;
    private Button mForgot;
    private Button mRegister;


    //Firebase Auth
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mLoginProgress;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(LoginViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Войти в аккаунт");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoginProgress = new ProgressDialog(this);

        mEmail = (TextInputLayout) findViewById(R.id.login_email_layout);
        inputEmail = (TextInputEditText) findViewById(R.id.login_email_input);
        inputPassword = (TextInputEditText) findViewById(R.id.input_password);
        mPassword = (TextInputLayout) findViewById(R.id.login_password_layout);
        mLogin = (Button) findViewById(R.id.login_button);
        mForgot = (Button) findViewById(R.id.login_forgot_button);
        mRegister = (Button) findViewById(R.id.login_register_button);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.onLoginClicked(Objects.requireNonNull(inputEmail.getText()).toString(), Objects.requireNonNull(inputPassword.getText()).toString());
            }
        });

        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPassword_Intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPassword_Intent);
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registration_Intent = new Intent(LoginActivity.this, RegisterPatientActivity.class);
                startActivity(registration_Intent);
            }
        });

        loginViewModel.isValidInputs.observe(this, new Observer(){

            @Override
            public void onChanged(@Nullable Object o) {
                Boolean isValid = (Boolean) o;
                showError(isValid);
            }
        });

        loginViewModel.isSuccessAuth.observe(this, new Observer(){

            @Override
            public void onChanged(@Nullable Object o) {
                Boolean isValid = (Boolean) o;

                if(Boolean.TRUE.equals(isValid)) {
                    mLoginProgress.dismiss();
                    Intent main_Intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(main_Intent);
                    Toast.makeText(LoginActivity.this, "Произведен вход в аккаунт", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Пароль и Email неверны", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showError(Boolean isValid) {
        if (!isValid) {
            Toast.makeText(LoginActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
        }
    }
}
