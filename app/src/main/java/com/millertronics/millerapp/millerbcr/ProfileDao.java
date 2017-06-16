package com.millertronics.millerapp.millerbcr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Koha Choji on 08/06/2017.
 */

public class ProfileDao extends SQLiteOpenHelper {

    private static final String TAG = ProfileDao.class.getName();

    private static final String APP_DATABASE = "miller_bcr.db";
    private static final String PROFILE_TABLE = "profiles";
    private static final String PROFILE_COL_ID = "id";
    private static final String PROFILE_COL_NAME = "name";
    private static final String PROFILE_COL_JOB_TITLE = "job_title";
    private static final String PROFILE_COL_COMPANY = "company";
    private static final String PROFILE_COL_PRIMARY_TEL = "primary_tel";
    private static final String PROFILE_COL_EMAIL = "email";
    private static ProfileDao mInstance;

    private ProfileDao(Context context) {
        super(context, APP_DATABASE, null, 1);
    }

    public static ProfileDao getInstance(Context context){
        if (mInstance == null){
            mInstance = new ProfileDao(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder createTableQueryBuilder = new StringBuilder();
        createTableQueryBuilder.append("create table ").append(PROFILE_TABLE)
                .append(" (")
                .append(PROFILE_COL_ID).append(" integer primary key")
                .append(", ")
                .append(PROFILE_COL_NAME).append(" text")
                .append(", ")
                .append(PROFILE_COL_JOB_TITLE).append(" text")
                .append(", ")
                .append(PROFILE_COL_COMPANY).append(" text")
                .append(", ")
                .append(PROFILE_COL_PRIMARY_TEL).append(" text")
                .append(", ")
                .append(PROFILE_COL_EMAIL).append(" text")
                .append(")");
        sqLiteDatabase.execSQL(createTableQueryBuilder.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO implement a proper upgrade task
        // (e.g. backup old database and insert to new schema)

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(sqLiteDatabase);
    }

    public boolean insert(Profile profile){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            setContentValuesFromProfile(values, profile);
            db.insert(PROFILE_TABLE, null, values);
        } catch(Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    public boolean update(Profile profile){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            setContentValuesFromProfile(values, profile);
            db.update(PROFILE_TABLE, values, "id = ?",
                    new String[]{ String.valueOf(profile.getId())});
        } catch(Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    private void setContentValuesFromProfile(ContentValues values, Profile profile){
        values.put(PROFILE_COL_NAME, profile.getName());
        values.put(PROFILE_COL_JOB_TITLE, profile.getJobTitle());
        values.put(PROFILE_COL_COMPANY, profile.getCompany());
        values.put(PROFILE_COL_PRIMARY_TEL, profile.getPrimaryContactNumber());
        values.put(PROFILE_COL_EMAIL, profile.getEmail());
    }

    public Cursor loadDataForMinimalList() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select ")
                    .append(PROFILE_COL_ID).append(", ")
                    .append(PROFILE_COL_NAME).append(", ")
                    .append(PROFILE_COL_COMPANY)
                    .append(" from ").append(PROFILE_TABLE)
                    .append(" order by ").append(PROFILE_COL_NAME).append(" asc")
                    .append(";");
            Cursor result =
                    db.rawQuery(queryBuilder.toString(), null);
            return result;
        } catch(Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    public Profile load(Integer id){
        Profile profile = null;
        if (id != null) {
            try {
                SQLiteDatabase db = this.getReadableDatabase();
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("select ")
                        .append(PROFILE_COL_NAME).append(", ")
                        .append(PROFILE_COL_JOB_TITLE).append(", ")
                        .append(PROFILE_COL_COMPANY).append(", ")
                        .append(PROFILE_COL_PRIMARY_TEL).append(", ")
                        .append(PROFILE_COL_EMAIL)
                        .append(" from ").append(PROFILE_TABLE)
                        .append(" where ").append(PROFILE_COL_ID).append(" = ?")
                        .append(";");
                Cursor result =
                        db.rawQuery(queryBuilder.toString(), new String[]{String.valueOf(id)});
                result.moveToFirst();
                profile = new Profile(
                        result.getString(result.getColumnIndex(PROFILE_COL_NAME)),
                        result.getString(result.getColumnIndex(PROFILE_COL_JOB_TITLE)),
                        result.getString(result.getColumnIndex(PROFILE_COL_COMPANY)),
                        result.getString(result.getColumnIndex(PROFILE_COL_PRIMARY_TEL)),
                        result.getString(result.getColumnIndex(PROFILE_COL_EMAIL))
                );
                profile.setId(id);
                result.close();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return profile;
    }
}
