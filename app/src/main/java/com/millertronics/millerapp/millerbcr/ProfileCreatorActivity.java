package com.millertronics.millerapp.millerbcr;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ProfileCreatorActivity extends AppCompatActivity {

    private Profile profile;

    private EditText nameInput;
    private EditText jobTitleInput;
    private EditText companyInput;
    private EditText telephoneInput;
    private EditText emailInput;

    private ProfileDao profileDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creator);

        nameInput = (EditText) findViewById(R.id.input_name);
        jobTitleInput = (EditText) findViewById(R.id.input_job_title);
        companyInput = (EditText) findViewById(R.id.input_company);
        telephoneInput = (EditText) findViewById(R.id.input_telephone);
        emailInput = (EditText) findViewById(R.id.input_email);

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

        profileDao = new ProfileDao(this, null);

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
            if (saveProfile(profile)) {
                showSaveSuccessDialog();
            } else {
                Utils.displayErrorDialog(this);
            }
        } else {
            alertInvalidProfile();
        }
    }

    private boolean saveProfile(Profile profile) {
        return profileDao.insert(profile);
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

    private void showSaveSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_success);
        builder.setMessage(R.string.profile_creator_save_success_message);
        builder.setCancelable(false); //Don't let them touch out!
        builder.setNegativeButton(R.string.dialog_ok,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ProfileCreatorActivity.this,
                                ProfileListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.setPositiveButton(R.string.profile_creator_save_success_scan_another,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ProfileCreatorActivity.this,
                                CameraReaderActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.create().show();
    }

    private boolean generateProfile() {
        ArrayList<String> profileData;
        try{
            profileData = getIntent().getStringArrayListExtra(CameraReaderActivity.PROFILE_DATA_KEY);
        } catch(Exception e){
            Log.w(ProfileCreatorActivity.class.getName(), Log.getStackTraceString(e));
            return false;
        }
        Map<String, Integer> phoneNumberCandidates = new HashMap<String, Integer>();
        Map<String, Integer> emailCandidates = new HashMap<String, Integer>();
        for (String snapshot : profileData){
            Log.d(ProfileCreatorActivity.class.getName(), snapshot);
            for (String text : snapshot.split("\n")){
                selectPhoneNumber(text, phoneNumberCandidates);
                selectEmail(text, emailCandidates);
            }
        }
        boolean generateProfile = false;
        String phoneNumber = getBestCandidate(phoneNumberCandidates);
        if (StringUtils.isNotBlank(phoneNumber)){
            generateProfile = true;
            telephoneInput.setText(phoneNumber);
        }
        String email = getBestCandidate(emailCandidates);
        if (StringUtils.isNotBlank(email)){
            generateProfile = true;
            emailInput.setText(email);
        }
        return generateProfile;
    }

    private void selectPhoneNumber(String text, Map<String, Integer> phoneNumberCandidates) {
        //At least 6 numbers, allow other characters
        if (text.matches("/(?:\\d+\\D+){5,}\\d+/")){
            String trimmed = text.trim();
            if (phoneNumberCandidates.containsKey(trimmed)){
                phoneNumberCandidates.put(trimmed, phoneNumberCandidates.get(trimmed)+1);
            } else {
                phoneNumberCandidates.put(trimmed, 1);
            }
        }
    }
    private void selectEmail(String text, Map<String, Integer> emailCandidates) {
        int atPos = text.indexOf("@");
        int dotPos = text.lastIndexOf(".");
        //Very basic check to see if a text COULD BE an email address
        if (atPos != -1 && dotPos > atPos){
            String trimmed = text.trim();
            if (emailCandidates.containsKey(trimmed)){
                emailCandidates.put(trimmed, emailCandidates.get(trimmed)+1);
            } else {
                emailCandidates.put(trimmed, 1);
            }
        }
    }

    private String getBestCandidate(Map<String, Integer> candidates){
        int maxValue = 0;
        String bestCandidate ="";
        for (Map.Entry<String, Integer> candidate : candidates.entrySet()){
            if (candidate.getValue() > maxValue){
                maxValue = candidate.getValue();
                bestCandidate = candidate.getKey();
            }
        }
        candidates.remove(bestCandidate);
        return bestCandidate;
    }

}
