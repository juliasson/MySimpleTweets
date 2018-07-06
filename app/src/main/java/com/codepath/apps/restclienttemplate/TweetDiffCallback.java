package com.codepath.apps.restclienttemplate;

import android.support.v7.util.DiffUtil;

import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetDiffCallback extends DiffUtil.Callback {

    private List<Tweet> mOldList;
    private List<Tweet> mNewList;

    public TweetDiffCallback(List<Tweet> oldList, List<Tweet> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }
    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // add a unique ID property on Contact and expose a getId() method
        return mOldList.get(oldItemPosition).getUid() == mNewList.get(newItemPosition).getUid();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Tweet oldItem = mOldList.get(oldItemPosition);
        Tweet newItem = mNewList.get(newItemPosition);

        return oldItem.getUser().equals(newItem.getUser())
                && (oldItem.getBody().equals(newItem.getBody())
                && (oldItem.getCreatedAt().equals(newItem.getCreatedAt())));
    }
}
