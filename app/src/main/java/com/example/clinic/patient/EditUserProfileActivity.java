package com.example.clinic.patient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clinic.R;

public class EditUserProfileActivity extends AppCompatActivity {
    private TextView mAge, mBloodGroup, mNumber, mAddress;

    private String age, bloodgroup, number, address;
    private Toolbar mToolbar;
    private Button actionUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        //Toolbar
        mToolbar = findViewById(R.id.edit_user);
        setSupportActionBar(mToolbar);

        mAge = findViewById(R.id.edit_user_age);
        mBloodGroup = findViewById(R.id.edit_user_bloodgroup);
        mNumber = findViewById(R.id.edit_user_number);
        mAddress = findViewById(R.id.edit_user_address);
        actionUpdate = findViewById(R.id.reg_button);

        age = getIntent().getStringExtra("Age");
        bloodgroup = getIntent().getStringExtra("Blood Group");
        number = getIntent().getStringExtra("Contact Number");
        address = getIntent().getStringExtra("Address");

        if (age == null) {
            age = "";
        }
        if (bloodgroup == null) {
            bloodgroup = "";
        }
        if (number == null) {
            number = "";
        }
        if (address == null) {
            address = "";
        }

        actionUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    public void update(View view) {

        switch (view.getId()) {

            case R.id.edit_age:
                alertDialog(age, "Age");
                break;

            case R.id.edit_address:
                alertDialog(address, "Address");
                break;

            case R.id.final_update:
                updateUserProfile();
                break;

            default:
                break;
        }

    }

    private void updateUserProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.update_alert_message);
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void alertDialog(String text, final String detail) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.update_dialog, null);


        TextView textView = (TextView) view.findViewById(R.id.update_textView);
        final EditText editText = (EditText) view.findViewById(R.id.editText);

        textView.setText(detail);
        editText.setText(text, TextView.BufferType.EDITABLE);

        builder.setView(view);
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String update = editText.getText().toString();

                if (detail.equals("Blood Group")) {
                    mBloodGroup.setText(update);
                    bloodgroup = mBloodGroup.getText().toString();
                } else if (detail.equals("Address")) {
                    mAddress.setText(update);
                    address = mAddress.getText().toString();
                } else if (detail.equals("Age")) {
                    mAge.setText(update);
                    age = mAge.getText().toString();
                } else if (detail.equals("Contact Number")) {
                    mNumber.setText(update);
                    number = mNumber.getText().toString();
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}

