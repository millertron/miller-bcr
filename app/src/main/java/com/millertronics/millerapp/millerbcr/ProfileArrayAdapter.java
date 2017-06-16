package com.millertronics.millerapp.millerbcr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Koha Choji on 15/06/2017.
 */

public class ProfileArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public static final String DELIMITER = "%break%";

    public ProfileArrayAdapter(Context context, String[] values){
        super(context, R.layout.list_profile, values);
        this.context =context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = infalter.inflate(R.layout.list_profile, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.profile_text);

        //Requires string to be in format <Display Text>%break%<Database ID>
        final String[] text = (values[position]).split(DELIMITER);

        textView.setText(text[0]);

        textView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent i = new Intent(context, ProfileViewActivity.class);
                i.putExtra(ProfileViewActivity.PROFILE_ID_KEY, Integer.valueOf(text[1]));
                context.startActivity(i);
            }
        });

        return rowView;
    }
}
