package com.hero.fragments.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hero.R;
import com.hero.model.Challenge;

import java.util.List;

public class ChallengeAdapter extends ArrayAdapter<Challenge>
{
    int mLayoutResource;

    Activity mActivity;
    List<Challenge> mItems;

    public ChallengeAdapter(Activity activity, List<Challenge> challenges, int itemLayout)
    {
        // Call super function with the item layout
        super(activity, itemLayout, challenges);

        // Cache for later
        mLayoutResource = itemLayout;

        // Set data members
        this.mItems = challenges;
        this.mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Prepare view holder
        ViewHolder viewHolder;

        // Don't have a cached view?
        if (convertView == null)
        {
            // Get inflater service
            LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Inflate the alert layout
            convertView = layoutInflater.inflate(mLayoutResource, null);

            // Create a new view holder
            viewHolder = new ViewHolder();

            // Cache the view resources
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);

            // Store it in tag
            convertView.setTag(viewHolder);
        }
        else
        {
            // Get cached convert view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Retrieve the current item
        Challenge item = mItems.get(position);

        // Set title
        viewHolder.title.setText(item.title);

        // Return the modified view
        return convertView;
    }

    public boolean hasStableIds()
    {
        // IDs are unique
        return true;
    }

    public static class ViewHolder
    {
        public TextView title;
    }
}