package com.millertronics.millerapp.millerbcr;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ProfileListActivity extends AppCompatActivity {

    private ProfileDao profileDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);
        profileDao = new ProfileDao(this, null);
        ListView listView = (ListView) findViewById(R.id.list_view);

        Cursor profileData = profileDao.loadDataForMinimalList();
        if (profileData == null){
            Utils.displayErrorDialog(this);
        }else {
            List<String> profileItems = new ArrayList<String>();
            for(profileData.moveToFirst(); !profileData.isAfterLast(); profileData.moveToNext()){
                StringBuilder sb = new StringBuilder();
                sb.append(profileData.getString(1))
                        .append(" / ")
                        .append(profileData.getString(2))
                        .append(ProfileArrayAdapter.DELIMITER)
                        .append(profileData.getString(0));
                profileItems.add(sb.toString());
            }
            profileData.close();

            ProfileArrayAdapter adapter = new ProfileArrayAdapter(
                    this,
                    profileItems.toArray(new String[profileItems.size()])
            );
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }


    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ProfileListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
