package com.example.clinic.patient;

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
        mToolbar = (Toolbar) findViewById(R.id.edit_user);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Редактировать информацию");

        mAge = (TextView) findViewById(R.id.edit_user_age);
        mBloodGroup = (TextView) findViewById(R.id.edit_user_bloodgroup);
        mNumber = (TextView) findViewById(R.id.edit_user_number);
        mAddress = (TextView) findViewById(R.id.edit_user_address);
        actionUpdate = (Button) findViewById(R.id.reg_button);

        age = getIntent().getStringExtra("Age").toString();
        bloodgroup = getIntent().getStringExtra("Blood Group").toString();
        number = getIntent().getStringExtra("Contact Number").toString();
        address = getIntent().getStringExtra("Address").toString();

        actionUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
    }

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

