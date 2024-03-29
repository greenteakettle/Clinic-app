package com.example.clinic.auth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {

    MutableLiveData<Boolean> isValidInputs = new MutableLiveData<>();
    MutableLiveData<Boolean> isSuccessAuth = new MutableLiveData<>();

    void onLoginClicked(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            isValidInputs.postValue(false);
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            boolean is = task.isSuccessful();

                            isSuccessAuth.postValue(task.isSuccessful());
                        }
                    });
        }
    }
}