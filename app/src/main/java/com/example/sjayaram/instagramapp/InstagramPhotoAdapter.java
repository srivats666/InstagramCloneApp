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

    public InstagramPhotoAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo  = getItem(position);
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        TextView tvUsername = (TextView)convertView.findViewById(R.id.tvUsername);
        ImageView ivProfile = (ImageView)convertView.findViewById(R.id.ivProfilePic);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tvDate);
        ImageView ivPhoto = (ImageView)convertView.findViewById(R.id.ivPhoto);
        TextView tvLikes = (TextView)convertView.findViewById(R.id.tvLikes);
        TextView tvCaption = (TextView)convertView.findViewById(R.id.tvCaption);
        //final VideoView vvVideo = (VideoView)convertView.findViewById(R.id.vvVideo);

        //set Details
        tvUsername.setText(photo.username);
        tvDate.setText(DateUtils.getRelativeTimeSpanString(photo.timeStamp * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        tvCaption.setText(photo.caption);
        DecimalFormat formatter = new DecimalFormat("#,###");
        tvLikes.setText(formatter.format(photo.likesCount) + " likes");

        //Set profile image
        ivProfile.setImageResource(0);
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(35)
                .oval(true)
                .build();

        Picasso.with(getContext())
                .load(photo.profile_picture)
                .resize(120, 0)
                .transform(transformation)
                .into(ivProfile);

        //Set image resource
        //if(photo.videoUrl==null) {
            //vvVideo.setVisibility(View.INVISIBLE);
            //ivPhoto.setVisibility(View.VISIBLE);
            ivPhoto.setImageResource(0);

            transformation = new RoundedTransformationBuilder()
                    //.borderColor(Color.BLACK)
                    //.borderWidthDp(1)
                    .cornerRadiusDp(25)
                    .scaleType(ImageView.ScaleType.CENTER_INSIDE)
                    .build();

            int width = DeviceDimensionsHelper.getDisplayWidth(getContext()) - 50;
            int height = DeviceDimensionsHelper.getDisplayHeight(getContext()) - 610;

            Picasso.with(getContext())
                    .load(photo.imageUrl)
                    .resize(width, height)
                    .placeholder(R.drawable.placeholder)
                    .transform(transformation)
                    .into(ivPhoto);
        //}
        /*else {
            //set video
            ivPhoto.setVisibility(View.INVISIBLE);
            vvVideo.setVisibility(View.VISIBLE);

            *//*int width = DeviceDimensionsHelper.getDisplayWidth(getContext()) - 50;
            int height = DeviceDimensionsHelper.getDisplayHeight(getContext()) - 610;

            vvVideo.setMinimumWidth(width);
            vvVideo.setMinimumHeight(height);*//*


            vvVideo.setVideoPath(photo.videoUrl);
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(vvVideo);
            vvVideo.setMediaController(mediaController);
            vvVideo.requestFocus();
            vvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    vvVideo.start();
                }
            });
        }*/

        tvCaption.setAutoLinkMask(0);
        Pattern hashTagsPattern = Pattern.compile("(#[a-zA-Z0-9_-]+)");

        //Scheme for Linkify, when a word matched tagMatcher pattern,
        //that word is appended to this URL and used as content URI
        String newActivityURL = "http://instagram.com";
        //Attach Linkify to TextView
        Linkify.addLinks(tvCaption, hashTagsPattern, newActivityURL);

        Pattern pattern = Pattern.compile("(@[A-Za-z0-9_-]+)");
        String scheme = "http://instagram.com/";
        Linkify.addLinks(tvCaption, pattern, scheme, null, mentionFilter);
        tvCaption.setLinkTextColor(Color.parseColor("#2792ff"));

        return convertView;
    }

    Linkify.TransformFilter mentionFilter = new Linkify.TransformFilter() {
        public final String transformUrl(final Matcher match, String url) {
            Log.i("tagstest", match.group(1));
            return match.group(1);
        }
    };
}
