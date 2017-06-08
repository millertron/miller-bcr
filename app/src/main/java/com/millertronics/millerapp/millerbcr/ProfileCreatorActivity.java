package com.millertronics.millerapp.millerbcr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ProfileCreatorActivity extends AppCompatActivity {

    private Profile profile;

    private EditText nameInput;
    private EditText jobTitleInput;
    private EditText companyInput;
    private EditText telephoneInput;
    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creator);

        if (!generateProfile()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.profile_creator_alert_read_fail);
            builder.setNeutralButton(R.string.profile_creator_alert_read_fail_retry,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ProfileCreatorActivity.this,
                                    CameraReaderActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.setNegativeButton(R.string.profile_creator_alert_read_fail_manual,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            builder.setPositiveButton(R.string.profile_creator_alert_read_fail_exit,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(ProfileCreatorActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            builder.create().show();
        }

        nameInput = (EditText) findViewById(R.id.input_name);
        jobTitleInput = (EditText) findViewById(R.id.input_job_title);
        companyInput = (EditText) findViewById(R.id.input_company);
        telephoneInput = (EditText) findViewById(R.id.input_telephone);
        emailInput = (EditText) findViewById(R.id.input_email);

        Button saveButton = (Button) findViewById(R.id.save_button);
        Button rescanButton = (Button) findViewById(R.id.rescan_button);
        Button exitButton = (Button) findViewById(R.id.exit_button);

        saveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                validateAndCreateProfile();
            }
        });
        rescanButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                confirmRescan();
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                confirmExit();
            }
        });
    }

    private void confirmRescan(){
        dialogConfirm(R.string.profile_creator_confirm_rescan,
                R.string.profile_creator_button_rescan,
                CameraReaderActivity.class);
    }

    private void confirmExit(){
        dialogConfirm(R.string.profile_creator_confirm_exit,
                R.string.profile_creator_button_exit,
                MainActivity.class);
    }

    private void dialogConfirm(int dialogMessage,
                               int confirmMessage,
                               final Class newActivityClass){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_confirmation);
        builder.setMessage(dialogMessage);
        builder.setNegativeButton(R.string.dialog_cancel,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.setPositiveButton(confirmMessage,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ProfileCreatorActivity.this,
                                newActivityClass);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.create().show();
    }

    private void validateAndCreateProfile(){

        Profile profile = new Profile(
                nameInput.getText().toString(),
                jobTitleInput.getText().toString(),
                companyInput.getText().toString(),
                telephoneInput.getText().toString(),
                emailInput.getText().toString()
        );
        if (profile.isValid()){
            saveProfile(profile);
        } else {
            alertInvalidProfile();
        }
    }

    private void saveProfile(Profile profile) {
    }

    private void alertInvalidProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.profile_creator_save_invalid_title);
        builder.setMessage(R.string.profile_creator_save_invalid_message);
        builder.setPositiveButton(R.string.dialog_ok,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }

    private boolean generateProfile(){
        return false;
    }
}
