package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity{
    Tweet tweet;
    Context context;
    private TwitterClient client;

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

        Tweet replyTweet; //TODO: need to get user data ;;

        TextView tvReplyUserName = replyView.findViewById(R.id.tvUserName);
        TextView tvReplyScreenName = replyView.findViewById(R.id.tvScreenName);
        final TextView tvReplyCharCount = replyView.findViewById(R.id.tvCharCount);
        final EditText etReplyTweetBody = replyView.findViewById(R.id.etTweetBody);
        ImageButton ibReplyCancel = replyView.findViewById(R.id.ibCancel);
        Button bvTweetButton = replyView.findViewById(R.id.bvTweetButton);

        tvReplyUserName.setText(tweet.user.name);
        tvReplyScreenName.setText(String.format("@%s", tweet.user.screenName));
        etReplyTweetBody.setText(String.format("@%s ", tweet.user.screenName));
        etReplyTweetBody.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // this will show characters remaining
                tvReplyCharCount.setText(String.valueOf(140 - s.toString().length()));
            }
        });

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

        ibReplyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        bvTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.sendTweet(etReplyTweetBody.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            tweet = Tweet.fromJSON(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("TwitterClient", errorResponse.toString());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("TwitterClient", responseString);
                        throwable.printStackTrace();
                    }
                });
            }
        });
        alertDialog.show();
    }
}
