package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity{
    Tweet tweet;
    Context context;

    TextView tvUserName;
    TextView tvScreenName;
    TextView tvTimeStamp;
    TextView tvBody;
    ImageView ivProfileImage;
    ImageButton ibReply;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        context = this;

        tvUserName = findViewById(R.id.tvUserName);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        tvBody = findViewById(R.id.tvBody);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ibReply = findViewById(R.id.ibReply);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Log.d("TweetDetailsActivity", "Showing details for tweet.");

        tvUserName.setText(tweet.user.name);
        tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        tvTimeStamp.setText(getRelativeTimeAgo(tweet.createdAt));
        tvBody.setText(tweet.body);
        ibReply.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showAlertDialogBox();
            }
        });

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(25,0)))
                .into(ivProfileImage);
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    private void showAlertDialogBox() {
        View replyView = LayoutInflater.from(context).inflate(R.layout.reply_tweet, null);
        ((TextView)replyView.findViewById(R.id.tvUserName)).setText(tweet.user.name);
        ((TextView)replyView.findViewById(R.id.tvScreenName)).setText(String.format("@%s", tweet.user.screenName));

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(25,0)))
                .into((ImageView)replyView.findViewById(R.id.ivProfileImage));

        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(replyView);

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
