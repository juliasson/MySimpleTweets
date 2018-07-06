package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends ListAdapter<Tweet, TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context mContext;
    public static final DiffUtil.ItemCallback<Tweet> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Tweet>() {
                @Override
                public boolean areItemsTheSame(Tweet oldItem, Tweet newItem) {
                    return oldItem.getUid() == newItem.getUid();
                }
                @Override
                public boolean areContentsTheSame(Tweet oldItem, Tweet newItem) {
                    return (oldItem.getUser().equals(newItem.getUser())
                            && (oldItem.getBody().equals(newItem.getBody())
                            && (oldItem.getCreatedAt().equals(newItem.getCreatedAt()))));
                }
            };

    /*
    //pass in the tweets array into constructor to use it
    public TweetAdapter (List<Tweet> tweets) {
        mTweets = tweets;
    }
    */

    public TweetAdapter() {
        super(DIFF_CALLBACK);
    }

    protected TweetAdapter(@NonNull DiffUtil.ItemCallback<Tweet> diffCallback) {
        super(diffCallback);
    }

    //for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    //bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get the data according to position
        Tweet tweet = getItem(position);

        //populate the views according to the data
        holder.tvUserName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        holder.tvTimeStamp.setText(getRelativeTimeAgo(tweet.createdAt));

        //loading the image
        Glide.with(mContext)
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(25,0)))
                .into(holder.ivProfileImage);
    }

    //create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvTimeStamp;
        public ImageButton ibReplyButton;

        public ViewHolder(View itemView) {
            super(itemView);

            //perform viewById look ups
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            ibReplyButton = itemView.findViewById(R.id.ibReply);
            ibReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, ComposeActivity.class);
                    Tweet tweet = mTweets.get(getAdapterPosition());
                    i.putExtra("screen_name", tweet.user.screenName);
                    mContext.startActivity(i);
                }
            });
            itemView.setOnClickListener(this);
        }

        //moving to a more detailed activity when movie is clicked
        @Override
        public void onClick(View view) {
            //get position & ensure validity
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                //get the movie at the valid position
                Tweet tweet = mTweets.get(position);
                //creating an intent to display MovieDetailsActivity
                Intent intent = new Intent(mContext, TweetDetailActivity.class);
                //passing the movie as an extra serialized via Parcel
                intent.putExtra("tweet", Parcels.wrap(tweet));
                //show the activity
                mContext.startActivity(intent);
            }
        }
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

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreTweets(List<Tweet> newTweets) {
        mTweets.addAll(newTweets);
        submitList(mTweets); // DiffUtil takes care of the check
    }

    public void swapItems(List<Tweet> contacts) {
        // compute diffs
        final TweetDiffCallback diffCallback = new TweetDiffCallback(this.mTweets, contacts);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // clear contacts and add
        this.mTweets.clear();
        this.mTweets.addAll(contacts);

        diffResult.dispatchUpdatesTo(this); // calls adapter's notify methods after diff is computed
    }
}
