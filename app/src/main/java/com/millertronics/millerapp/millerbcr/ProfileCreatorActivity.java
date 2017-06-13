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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ProfileCreatorActivity extends AppCompatActivity {

    private Profile profile;

    private EditText nameInput;
    private EditText jobTitleInput;
    private EditText companyInput;
    private EditText telephoneInput;
    private EditText emailInput;
    private Button nameCandidatesButton;
    private Button jobTitleCandidatesButton;
    private Button companyCandidatesButton;
    private Button telephoneCandidatesButton;
    private Button emailCandidatesButton;

    Map<String, Integer> phoneNumberCandidates = new HashMap<String, Integer>();
    Map<String, Integer> emailCandidates = new HashMap<String, Integer>();
    List<String> genericCandidates = new ArrayList<String>();

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

        nameCandidatesButton = (Button) findViewById(R.id.name_candidates_button);
        jobTitleCandidatesButton = (Button) findViewById(R.id.job_title_candidates_button);
        companyCandidatesButton = (Button) findViewById(R.id.company_candidates_button);
        telephoneCandidatesButton = (Button) findViewById(R.id.telephone_candidates_button);
        emailCandidatesButton = (Button) findViewById(R.id.email_candidates_button);

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
            builder.show();
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

        nameCandidatesButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                popUpCandidates(genericCandidates, nameInput);
            }
        });
        jobTitleCandidatesButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                popUpCandidates(genericCandidates, jobTitleInput);
            }
        });
        companyCandidatesButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                popUpCandidates(genericCandidates, companyInput);
            }
        });
        telephoneCandidatesButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                popUpCandidates(phoneNumberCandidates.keySet(), telephoneInput);
            }
        });
        emailCandidatesButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                popUpCandidates(emailCandidates.keySet(), emailInput);
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
        builder.show();
    }

    private void popUpCandidates(Collection<String> candidates, final EditText input){
        if (!candidates.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final CharSequence[] items = candidates.toArray(new CharSequence[candidates.size()]);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInteface, int i) {
                    input.setText(items[i]);
                }
            });
            builder.show();
        }
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
        } catch(Exception e) {
            Log.w(ProfileCreatorActivity.class.getName(), Log.getStackTraceString(e));
            return false;
        }

        for (String snapshot : profileData){
            Log.d(ProfileCreatorActivity.class.getName(), snapshot);
            for (String text : snapshot.split("\n")){
                boolean selected = false;
                selected = selectPhoneNumber(text, phoneNumberCandidates)
                        || selectEmail(text, emailCandidates);
                if (!selected) {
                    selectRest(text, genericCandidates);
                }
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
        for (int i = 0; i < genericCandidates.size(); i++){
            switch(i){
                case 0:
                    nameInput.setText(genericCandidates.get(i));
                    generateProfile = true;
                    break;
                case 1:
                    jobTitleInput.setText(genericCandidates.get(i));
                    break;
                case 2:
                    companyInput.setText(genericCandidates.get(i));
                    break;
                default:
                    break;
            }
        }
        genericCandidates.addAll(phoneNumberCandidates.keySet());
        genericCandidates.addAll(emailCandidates.keySet());
        return generateProfile;
    }

    private void selectRest(String text, List<String> genericCandidates) {
        List<String> toFilter = new ArrayList<String>();
        boolean filter = false;
        for (String candidate : genericCandidates){
            if (candidate.contains(text)){
                filter = true;
                break;
            }
            if (text.contains(candidate)){
                toFilter.add(candidate);
            }
        }
        if (!filter){
            genericCandidates.add(text);
        }
        genericCandidates.removeAll(toFilter);
    }

    private boolean selectPhoneNumber(String text, Map<String, Integer> phoneNumberCandidates) {
        //At least 6 numbers, allow other characters
        String trimmed = text.toLowerCase().replaceAll("tel:","").replaceAll("mob:","").trim();
        if (phoneNumberCandidates.containsKey(trimmed)) {
            phoneNumberCandidates.put(trimmed, phoneNumberCandidates.get(trimmed) + 1);
        } else {
            int numCount = 0;

            for (char c : trimmed.toCharArray()) {
                if (Character.isDigit(c)) {
                    numCount++;
                }
                if (numCount == 6) {
                    phoneNumberCandidates.put(trimmed, 1);
                    return true;
                }
            }
        }
        return false;
    }
    private boolean selectEmail(String text, Map<String, Integer> emailCandidates) {
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
            return true;
        }
        return false;
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
        //candidates.remove(bestCandidate);
        return bestCandidate;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ProfileCreatorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
