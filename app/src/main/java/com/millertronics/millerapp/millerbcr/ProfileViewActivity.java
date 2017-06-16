package com.millertronics.millerapp.millerbcr;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileViewActivity extends AppCompatActivity {

    Profile profile;
    public static final String PROFILE_ID_KEY = "profile_id_key";

    private ProfileDao profileDao;

    private TextView textViewName;
    private TextView textViewJobTitle;
    private TextView textViewCompany;
    private TextView textViewTelephone;
    private TextView textViewEmail;

    private Button returnToListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        profileDao = new ProfileDao(this, null);
        profile = loadProfile();
        if (profile == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.profile_creator_alert_read_fail);
            builder.setPositiveButton(R.string.profile_viewer_return_to_list_button,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            returnToList();
                        }
                    });
            builder.show();
        } else {
            textViewName = (TextView) findViewById(R.id.text_view_name);
            textViewJobTitle = (TextView) findViewById(R.id.text_view_job_title);
            textViewCompany = (TextView) findViewById(R.id.text_view_company);
            textViewTelephone = (TextView) findViewById(R.id.text_view_telephone);
            textViewEmail = (TextView) findViewById(R.id.text_view_email);

            textViewName.setText(profile.getName());
            textViewJobTitle.setText(profile.getJobTitle());
            textViewCompany.setText(profile.getCompany());
            textViewTelephone.setText(profile.getPrimaryContactNumber());
            textViewEmail.setText(profile.getEmail());
        }

        returnToListButton = (Button) findViewById(R.id.return_to_list_button);
        returnToListButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                returnToList();
            }
        });
    }

    private void returnToList() {
        Intent intent = new Intent(ProfileViewActivity.this, ProfileListActivity.class);
        startActivity(intent);
        finish();
    }

    private Profile loadProfile(){
        Profile profile = null;
        try {
            int profileId = getIntent().getIntExtra(PROFILE_ID_KEY, -1);
            if (profileId < 0){
                Log.d(ProfileViewActivity.class.getName(),
                        "Profile ID is not passed on from the previous activity");
            } else {
                profile = profileDao.load(profileId);
            }
        } catch (Exception e){
            Log.e(ProfileViewActivity.class.getName(), "Failed to load profile!");
            Log.e(ProfileViewActivity.class.getName(), Log.getStackTraceString(e));
        }
        return profile;
    }

    public void onBackPressed(){
        returnToList();
    }
}
