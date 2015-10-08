package com.hero.fragments.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hero.R;
import com.hero.config.Logging;
import com.hero.model.Challenge;
import com.hero.ui.images.CircularImageTransformation;
import com.hero.utils.formatting.StringUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class ChallengeAdapter extends ArrayAdapter<Challenge>
{
    int mLayoutResource;

    Activity mContext;
    List<Challenge> mItems;

    public ChallengeAdapter(Activity activity, List<Challenge> challenges, int itemLayout)
    {
        // Call super function with the item layout
        super(activity, itemLayout, challenges);

        // Cache for later
        mLayoutResource = itemLayout;

        // Set data members
        this.mItems = challenges;
        this.mContext = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Prepare view holder
        final ViewHolder viewHolder;

        // Don't have a cached view?
        if (convertView == null)
        {
            // Get inflater service
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Inflate the alert layout
            convertView = layoutInflater.inflate(mLayoutResource, null);

            // Create a new view holder
            viewHolder = new ViewHolder();

            // Cache the view resources
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.category = (TextView) convertView.findViewById(R.id.category);
            viewHolder.loading = (ProgressBar) convertView.findViewById(R.id.loading);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.creatorName = (TextView) convertView.findViewById(R.id.creatorName);
            viewHolder.creatorImage = (ImageView) convertView.findViewById(R.id.creatorImage);

            // Change progress bar color (we need @color/accent to be grey for sidebar)
            viewHolder.loading.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN);

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
        viewHolder.category.setText(item.category);
        viewHolder.description.setText(item.description);

        // Convert to human-readable date
        String date = DateFormat.getDateFormat(mContext).format(new Date(item.creationDate));

        // Display it
        viewHolder.date.setText(date);

        // Got a creator?
        if ( item.creator != null )
        {
            // Set creator name
            viewHolder.creatorName.setText(item.creator.name);

            // Challenge has an image?
            if (!StringUtils.stringIsNullOrEmpty(item.image))
            {
                // Load image with Picasso
                Picasso.with(mContext).load(item.image).into(viewHolder.image, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        // Hide loading
                        viewHolder.loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError()
                    {
                        // Log it
                        Log.e(Logging.TAG, "Image load failed");
                    }
                });
            }
        }

        // Creator has an image?
        if ( !StringUtils.stringIsNullOrEmpty(item.creator.image))
        {
            // Load image with Picasso
            Picasso.with(mContext).load(item.creator.image).transform(new CircularImageTransformation()).into(viewHolder.creatorImage);
        }

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
        public TextView date;
        public TextView description;
        public TextView category;
        public TextView creatorName;

        public ImageView image;
        public ImageView creatorImage;

        public ProgressBar loading;
    }
}