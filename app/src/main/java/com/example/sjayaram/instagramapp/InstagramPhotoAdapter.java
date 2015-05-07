package com.example.sjayaram.instagramapp;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sjayaram on 5/4/2015.
 */
public class InstagramPhotoAdapter extends ArrayAdapter<InstagramPhoto> {

    private static class ViewHolder {
        TextView userName;
        ImageView userProfile;
        TextView date;
        ImageView photo;
        TextView likes;
        TextView caption;
        VideoView video;
    }


    public InstagramPhotoAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo  = getItem(position);
        final ViewHolder viewHolder;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.userProfile = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.video = (VideoView) convertView.findViewById(R.id.vvVideo);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.caption = (TextView) convertView.findViewById(R.id.tvCaption);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //set Details
        viewHolder.userName.setText(photo.username);
        viewHolder.date.setText(DateUtils.getRelativeTimeSpanString(photo.timeStamp * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        viewHolder.caption.setText(photo.caption);
        DecimalFormat formatter = new DecimalFormat("#,###");
        viewHolder.likes.setText(formatter.format(photo.likesCount) + " likes");

        //Set profile image
        viewHolder.userProfile.setImageResource(0);
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(35)
                .oval(true)
                .build();

        Picasso.with(getContext())
                .load(photo.profile_picture)
                .resize(120, 0)
                .transform(transformation)
                .into(viewHolder.userProfile);

        //Set image resource
        if(photo.videoUrl==null) {
            viewHolder.video.setVisibility(View.INVISIBLE);
            viewHolder.photo.setVisibility(View.VISIBLE);
            viewHolder.photo.setImageResource(0);

            transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(25)
                    .scaleType(ImageView.ScaleType.CENTER_INSIDE)
                    .build();

            int width = DeviceDimensionsHelper.getDisplayWidth(getContext()) - 50;
            int height = DeviceDimensionsHelper.getDisplayHeight(getContext()) - 610;

            Picasso.with(getContext())
                    .load(photo.imageUrl)
                    .resize(width, height)
                    .transform(transformation)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.photo);
        }
        else {
            //set video
            viewHolder.photo.setVisibility(View.INVISIBLE);
            viewHolder.video.setVisibility(View.VISIBLE);
            //viewHolder.video.setImageResource(0);

            int width = DeviceDimensionsHelper.getDisplayWidth(getContext()) - 50;
            int height = DeviceDimensionsHelper.getDisplayHeight(getContext()) - 610;

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewHolder.video.getLayoutParams();

            layoutParams.height = height;
            layoutParams.width = width;
            viewHolder.video.setLayoutParams(layoutParams);

            viewHolder.video.setVideoPath(photo.videoUrl);
            MediaController mediaController = new MediaController(getContext());
            //mediaController.setAnchorView(viewHolder.video);
            viewHolder.video.setMediaController(mediaController);
            viewHolder.video.requestFocus();
            viewHolder.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    viewHolder.video.start();
                }
            });
        }

        viewHolder.caption.setAutoLinkMask(0);
        Pattern hashTagsPattern = Pattern.compile("(#[a-zA-Z0-9_-]+)");

        //Scheme for Linkify, when a word matched tagMatcher pattern,
        //that word is appended to this URL and used as content URI
        String newActivityURL = "http://instagram.com";
        //Attach Linkify to TextView
        Linkify.addLinks(viewHolder.caption, hashTagsPattern, newActivityURL);

        Pattern pattern = Pattern.compile("(@[A-Za-z0-9_-]+)");
        String scheme = "http://instagram.com/";
        Linkify.addLinks(viewHolder.caption, pattern, scheme, null, mentionFilter);
        viewHolder.caption.setLinkTextColor(Color.parseColor("#2792ff"));

        return convertView;
    }

    Linkify.TransformFilter mentionFilter = new Linkify.TransformFilter() {
        public final String transformUrl(final Matcher match, String url) {
            Log.i("tagstest", match.group(1));
            return match.group(1);
        }
    };
}
